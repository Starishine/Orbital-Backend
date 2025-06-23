import pandas as pd
from fastapi import FastAPI
from pydantic import BaseModel
from sklearn.feature_extraction.text import TfidfVectorizer
from sklearn.naive_bayes import MultinomialNB
from fastapi.middleware.cors import CORSMiddleware
from fastapi import Body

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
    data = pd.concat([data, pd.DataFrame([new_row])], ignore_index=True)
    descriptions = data["description"].tolist()
    categories = data["category"].tolist()
    # Re-instantiate vectorizer and model for retraining
    vectorizer = TfidfVectorizer()
    X = vectorizer.fit_transform(descriptions)
    model = MultinomialNB()
    model.fit(X, categories)
    # Save updated data to CSV for persistence
    data.to_csv("training_data.csv", index=False)
    return {"message": "Model retrained with new feedback."}
