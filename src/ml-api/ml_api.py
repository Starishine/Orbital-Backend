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
    # Load data only when needed
    data = pd.read_csv("training_data.csv")
    new_row = {"description": feedback.description.lower().strip(), "category": feedback.correct_category}
    upweight_factor = 10
    data = pd.concat([data, pd.DataFrame([new_row] * upweight_factor)], ignore_index=True)
    # Save updated data to CSV for persistence
    data.to_csv("training_data.csv", index=False)

    # Retrain model and vectorizer on the fly
    descriptions = data["description"].tolist()
    categories = data["category"].tolist()
    vectorizer_local = TfidfVectorizer()
    X_local = vectorizer_local.fit_transform(descriptions)
    model_local = MultinomialNB()
    model_local.fit(X_local, categories)

    # Test prediction on the feedback description
    norm_desc = feedback.description.lower().strip()
    test_vec = vectorizer_local.transform([norm_desc])
    new_pred = model_local.predict(test_vec)[0]

    # Release memory
    del data, descriptions, categories, X_local, vectorizer_local, model_local
    import gc
    gc.collect()
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



    