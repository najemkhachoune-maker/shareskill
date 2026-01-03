import requests
import uuid
import time
import json

BASE_URL = "http://localhost:8080/api"

def register_and_login(username, email, password):
    # Register
    url = f"{BASE_URL}/auth/register"
    payload = {
        "username": username,
        "email": email,
        "password": password,
        "firstName": "Test",
        "lastName": "User"
    }
    try:
        resp = requests.post(url, json=payload)
        resp.raise_for_status()
    except requests.exceptions.HTTPError as e:
        # If user already exists, try logging in
        if resp.status_code != 400: # Assuming 400 is "User already exists"
             print(f"Registration failed for {username}: {e}")

    # Login
    url = f"{BASE_URL}/auth/login"
    payload = {
        "email": email,
        "password": password
    }
    resp = requests.post(url, json=payload)
    resp.raise_for_status()
    data = resp.json()
    return data["accessToken"], data["userId"]

def post_review(token, reviewer_id, target_id):
    url = f"{BASE_URL}/reputation/reviews"
    headers = {
        "Authorization": f"Bearer {token}",
        "Content-Type": "application/json"
    }
    payload = {
        "reviewerId": reviewer_id,
        "targetUserId": target_id,
        "bookingId": str(uuid.uuid4()), # Simulating a booking ID
        "rating": 5,
        "title": "Excellent Service",
        "body": "This user is a highly competent professional!",
        "isVerified": True
    }
    resp = requests.post(url, json=payload, headers=headers)
    if resp.status_code == 201:
        print(f"✅ Review posted by {reviewer_id}")
    else:
        print(f"❌ Failed to post review: {resp.status_code} - {resp.text}")

def check_badges(token, target_id):
    url = f"{BASE_URL}/reputation/users/{target_id}/badges"
    headers = {"Authorization": f"Bearer {token}"}
    resp = requests.get(url, headers=headers)
    if resp.status_code == 200:
        badges = resp.json()
        print(f"\n🏆 Badges for Target User ({len(badges)} found):")
        for b in badges:
            print(f" - {b['name']}: {b['description']}")
        
        has_competent = any(b['name'] == 'Competent Professional' for b in badges)
        if has_competent:
            print("\n✅ SUCCESS: 'Competent Professional' badge found!")
        else:
            print("\n❌ 'Competent Professional' badge NOT found automatically.")
    else:
        print(f"Failed to fetch badges: {resp.status_code}")

def main():
    print("--- Starting Real-World Reputation & Badge Test ---\n")

    # 1. Register/Login Target User
    print("1. Setting up Target User...")
    target_token, target_id = register_and_login("TargetPro", "target_pro@test.com", "password123")
    print(f"Target User ID: {target_id}\n")

    # 2. Register Reviewers and Post Reviews
    # We need 4 reviews to trigger the badge (minReviews=4)
    print("2. Simulating 4 different users verifying competence...")
    
    reviewers = [
        ("Reviewer1", "rev1@test.com"),
        ("Reviewer2", "rev2@test.com"),
        ("Reviewer3", "rev3@test.com"),
        ("Reviewer4", "rev4@test.com")
    ]

    for username, email in reviewers:
        print(f"   > Processing {username}...")
        token, reviewer_id = register_and_login(username, email, "password123")
        post_review(token, reviewer_id, target_id)
        time.sleep(0.5) # Compliance delay

    # 3. Check Results
    print("\n3. Verifying Results...")
    check_badges(target_token, target_id)

if __name__ == "__main__":
    main()
