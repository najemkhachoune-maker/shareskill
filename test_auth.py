import requests
import json

url = "http://localhost:8081/api/auth/register"
payload = {
    "email": "final_success@test.com",
    "username": "final_success_user",
    "password": "password123",
    "firstName": "Success",
    "lastName": "User"
}
headers = {
    "Content-Type": "application/json"
}

try:
    response = requests.post(url, data=json.dumps(payload), headers=headers)
    print(f"Status Code: {response.status_code}")
    print(f"Response: {response.text}")
except Exception as e:
    print(f"Error: {e}")
