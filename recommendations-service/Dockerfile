# Start with a base Python image (e.g., Debian slim)
FROM python:3.12-slim-bookworm

WORKDIR /app

# --- 1. Install build tools (if needed) ---
# These are needed for packages like scikit-learn, numpy, etc.
# Keep them here, but if your base image changes frequently, this layer might rebuild.
# You can comment this out if you're certain your base image already provides these
# or if your libraries don't need compilation on a glibc-based system.
RUN apt-get update && apt-get install -y --no-install-recommends \
    build-essential \
    libatlas-base-dev \ 
    gfortran \
    && rm -rf /var/lib/apt/lists/* # Clean up apt cache to keep image size down

# --- 2. Copy and Install Python Dependencies (requirements.txt) ---
# This is crucial for caching. If requirements.txt doesn't change,
# pip install step is skipped in subsequent builds.
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# --- 3. Copy the Large Model File (trained_model.pkl) ---
# Copy this *after* requirements.txt but *before* general application code.
# If only your application code changes, this layer is still cached.
# Assuming model/trained_model.pkl is relative to your build context (recommendations-service folder)
# And you want it in /app/model/
COPY model/trained_model.pkl model/

# --- 4. Copy the Rest of Your Application Code ---
# This layer changes most frequently. By putting it last,
# previous layers are likely cached if only code changes.
COPY . .

EXPOSE 8086

# Your application's main command
# (Make sure main.py imports from model.recommendation or model.train correctly)
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8086"]