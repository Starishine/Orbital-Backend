import pandas as pd
import sys
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn.compose import ColumnTransformer
from sklearn.pipeline import Pipeline
from sklearn.ensemble import RandomForestClassifier  # Or LogisticRegression
from sklearn.metrics import classification_report
from sklearn.preprocessing import LabelEncoder
import joblib
from xgboost import XGBClassifier


# Load data
df = pd.read_csv("budget_insights.csv")


# Features & target
X = df.drop(columns=["label"])
y = df["label"]

# Define column types
categorical_cols = ["category", "budgetduration", "spendinginterval", "currency"]
numerical_cols = ["allocated", "spent", "percent_used", "streak"]

# Preprocessing
preprocessor = ColumnTransformer([
    ("cat", OneHotEncoder(handle_unknown="ignore"), categorical_cols),
    ("num", StandardScaler(), numerical_cols)
])

# if len(df) > 10000:
#     df = df.sample(n=10000, random_state=42)

# Create a pipeline
pipeline = Pipeline(steps=[
    ("preprocessor", preprocessor),
    ("classifier", XGBClassifier(use_label_encoder=False, eval_metric='mlogloss'))
])

# Encode string labels to integers
print("Encoding labels")
le = LabelEncoder()
y_encoded = le.fit_transform(y)

print("Splitting data")
#Train-test split
X_train, X_test, y_train, y_test = train_test_split(X, y_encoded, test_size=0.2, stratify=y_encoded, random_state=42)

print("Training model")
#Train the model
pipeline.fit(X_train, y_train)

print("Evaluating model")
# Evaluate the model
y_pred = pipeline.predict(X_test)
print(classification_report(y_test, y_pred))

print("Saving model")
joblib.dump(pipeline, "budget_insights_model.pkl")
joblib.dump(le, "label_encoder.pkl")

print("Done! Model saved as budget_insights_model.pkl")