import pandas as pd
from fastapi import FastAPI
from pydantic import BaseModel
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from fastapi.middleware.cors import CORSMiddleware
from fastapi import Body
from typing import List, Optional
import joblib
import numpy as np
from xgboost import XGBClassifier

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["http://localhost:8081"], 
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

data = pd.read_csv("training_data.csv")
data["description"] = data["description"].str.lower().str.strip()
descriptions = data["description"].tolist()
categories = data["category"].tolist()

vectorizer = TfidfVectorizer()
X = vectorizer.fit_transform(descriptions)

model = MultinomialNB()
model.fit(X, categories)

class ExpenseInput(BaseModel):
    description: str

@app.post("/categorize")
def categorize_expense(expense: ExpenseInput):
    input_text = [expense.description.lower().strip()]
    input_vec = vectorizer.transform(input_text)
    probas = model.predict_proba(input_vec)[0]
    top_indices = probas.argsort()[-3:][::-1]
    top_categories = [
        {"category": model.classes_[i], "confidence": round(probas[i], 2)}
        for i in top_indices
    ]
    return {
        "suggested": top_categories[0]["category"],
        "top_predictions": top_categories
    }


class FeedbackInput(BaseModel):
    description: str
    correct_category: str

@app.post("/feedback")
def feedback(feedback: FeedbackInput = Body(...)):
    global data, descriptions, categories, vectorizer, X, model
    new_row = {"description": feedback.description.lower().strip(), "category": feedback.correct_category}
    # Use pd.concat instead of append (append is deprecated)
    upweight_factor = 500
    data = pd.concat([data, pd.DataFrame([new_row] * upweight_factor)], ignore_index=True)
    descriptions = data["description"].tolist()
    categories = data["category"].tolist()
    # Re-instantiate vectorizer and model for retraining
    vectorizer = TfidfVectorizer()
    X = vectorizer.fit_transform(descriptions)
    model = MultinomialNB()
    model.fit(X, categories)
    # Save updated data to CSV for persistence
    data.to_csv("training_data.csv", index=False)
    
    # Debug printout: check prediction on the feedback description
    norm_desc = feedback.description.lower().strip()
    test_vec = vectorizer.transform([norm_desc])
    new_pred = model.predict(test_vec)[0]
    print(f"Added row: {new_row}")
    print(f"Prediction immediately after feedback: {new_pred}")
    print(f"Top 3 probs: {model.predict_proba(test_vec)[0]}")
    print(f"Data shape after append: {data.shape}")
    return {
        "message": "Model retrained with new feedback.",
        "new_prediction": new_pred
    }

# Load pre-trained pipeline
pipeline = joblib.load("budget_insights_model.pkl")
label_encoder = joblib.load("label_encoder.pkl")

# Define Budget schema
class Budget(BaseModel):
    category: str
    budgetduration: str  # 'daily', 'weekly', 'monthly', 'yearly'
    allocated: float
    spent: float
    currency: str
    spendinginterval: str  # 'daily', 'weekly', 'monthly', 'yearly'
    percent_used: float
    streak: Optional[int] = 0  # Number of periods under budget


@app.post("/insights")
async def generate_insights(budgets: List[Budget]):
    results = []
    for b in budgets:
        # Handle zero-allocated budgets
        if b.allocated == 0:
            results.append({
                "category": b.category,
                "prediction": "invalid_budget",
                "probabilities": {},
                "message": "Allocated budget is zero. Please set a positive budget amount for meaningful insights.",
                "streak": b.streak
            })
            continue

       # Predict for each budget individually
        df = pd.DataFrame([b.dict()])
        pred_int = pipeline.predict(df)[0]
        probs = pipeline.predict_proba(df)[0]
        pred_label = label_encoder.inverse_transform([pred_int])[0]
        class_labels = label_encoder.inverse_transform(np.arange(len(probs)))

        top2_idx = probs.argsort()[-2:][::-1]
        top2 = [
            {"label": str(class_labels[i]), "probability": float(probs[i])}
            for i in top2_idx
        ]
        print(f"Category: {b.category}, Top 2 Predictions: {top2}")
        # Convert all probabilities to float for JSON serialization
        results.append({
            "category": b.category,
            "prediction": pred_label,
            "probabilities": {str(label): float(prob) for label, prob in zip(class_labels, probs)},
            "streak": b.streak
        })

        print(f"Category: {b.category}, Prediction: {pred_label}, Probabilities: {probs}")

    return {"insights": results}

    # suggestions = []  

    # for budget in budgets:
    #     # Normalize budget duration
    #     budget_duration = budget.budgetduration.lower()
    #     if budget_duration in ['monthly', 'month']:
    #         budget_duration = 'monthly'
    #     elif budget_duration in ['weekly', 'week']:
    #         budget_duration = 'weekly'
    #     elif budget_duration in ['daily', 'day']:
    #         budget_duration = 'daily'
    #     elif budget_duration in ['yearly', 'year']:
    #         budget_duration = 'yearly'
    #     else:
    #         budget_duration = 'monthly'

    #     # Normalize spending interval
    #     spending_interval = budget.spendinginterval.lower()
    #     if spending_interval in ['monthly', 'month']:
    #         spending_interval = 'monthly'
    #     elif spending_interval in ['weekly', 'week']:
    #         spending_interval = 'weekly'
    #     elif spending_interval in ['daily', 'day']:
    #         spending_interval = 'daily'
    #     elif spending_interval in ['yearly', 'year']:
    #         spending_interval = 'yearly'
    #     else:
    #         spending_interval = 'monthly'

    #     # Avoid division by zero
    #     allocated = budget.allocated if budget.allocated > 0 else 1
    #     spent = budget.spent if budget.spent >= 0 else 0
    #     percent = (spent / allocated) * 100 if allocated else 0
    #     homeCurrency = budget.currency
    #     savings = allocated - spent

    #     # Suggestions based on budget duration and spending interval
    #     if spent > allocated:
    #         suggestions.append({
    #             "type": "overspending",
    #             "category": budget.category,
    #             "message": f"You have exceeded your {budget_duration} budget for {budget.category.lower()} by {homeCurrency} {spent - allocated:.2f}. You typically spend on this category {spending_interval}."
    #         })
    #     elif allocated - spent > 1e-2:
    #         suggestions.append({
    #             "type": "saving",
    #             "category": budget.category,
    #             "message": f"You are under your {budget_duration} budget for {budget.category.lower()} by {homeCurrency} {allocated - spent:.2f}. You typically spend on this category {spending_interval}."
    #         })

    #     if percent > 120:
    #         suggestions.append({
    #             "type": "overspending",
    #             "category": budget.category,
    #             "message": f"You have exceeded your {budget_duration} budget for {budget.category.lower()} by {percent-100:.1f}%. You typically spend on this category {spending_interval}."
    #         })
    #     elif percent > 90:
    #         suggestions.append({
    #             "type": "warning",
    #             "category": budget.category,
    #             "message": f"Warning: You have used {percent:.1f}% of your {budget_duration} budget for {budget.category.lower()}. You typically spend on this category {spending_interval}."
    #         })

    #     if spent < allocated * 0.5:
    #         suggestions.append({
    #             "type": "underspending",
    #             "category": budget.category,
    #             "message": f"You consistently underspend on {budget.category.lower()}. Consider reallocating your {budget_duration} budget. You typically spend on this category {spending_interval}."
    #         })

    #     if hasattr(budget, "streak") and budget.streak and budget.streak > 1:
    #         suggestions.append({
    #             "type": "streak",
    #             "category": budget.category,
    #             "message": f"Great job! You've stayed under your {budget_duration} {budget.category.lower()} budget for {budget.streak} {spending_interval}s in a row! You typically spend on this category {spending_interval}."
    #         })

    # return {"suggestions": suggestions}



    