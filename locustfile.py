from locust import HttpUser, task, between
import random
import string

class CitizenBot(HttpUser):
    weight = 10
    wait_time = between(1, 3) 
    host = "http://localhost:8080" 

    def on_start(self):
        random_string = ''.join(random.choices(string.ascii_lowercase, k=8))
        self.email = f"citizen_{random_string}@test.com"
        self.password = "Password123!"
        
        # 1. Register the bot as a Citizen
        self.client.post("/api/v1/auth/register", json={
            "email": self.email,
            "password": self.password,
            "firstName": "CitizenBot",
            "lastName": random_string
        })

        # 2. Log in to get the JWT token
        response = self.client.post("/api/v1/auth/login", json={
            "email": self.email,
            "password": self.password
        })

        if response.status_code == 200:
            token = response.json().get("data", {}).get("token")
            self.headers = {"Authorization": f"Bearer {token}"}
        else:
            self.headers = {}
            
        self.known_report_ids = []

    @task(4)
    def view_nearby_reports(self):
        """View the map and get nearby reports."""
        if not getattr(self, 'headers', None): return
        lat = -12.0464 + random.uniform(-0.02, 0.02)
        lng = -77.0428 + random.uniform(-0.02, 0.02)
        
        response = self.client.get(
            f"/api/v1/reports/nearby?latitude={lat}&longitude={lng}&radius=5000", 
            headers=self.headers,
            name="/api/v1/reports/nearby"
        )
        if response.status_code == 200:
            reports = response.json().get("data", [])
            self.known_report_ids = [r.get("id") for r in reports if r.get("id")]

    @task(1)
    def create_report(self):
        """Find a problem and report it."""
        if not getattr(self, 'headers', None): return
        lat = -12.0464 + random.uniform(-0.02, 0.02)
        lng = -77.0428 + random.uniform(-0.02, 0.02)
        categories = [1, 2, 3, 4]
        
        self.client.post("/api/v1/reports", json={
            "categoryId": random.choice(categories),
            "title": "Reporte detectado (Bot)",
            "description": "Reporte generado por Locust CitizenBot.",
            "latitude": lat,
            "longitude": lng,
            "imageUrl": None
        }, headers=self.headers)
        
    @task(2)
    def view_leaderboard(self):
        if not getattr(self, 'headers', None): return
        self.client.get("/api/v1/users/leaderboard", headers=self.headers)

    @task(2)
    def endorse_report(self):
        """Endorse a random known report."""
        if not getattr(self, 'headers', None) or not self.known_report_ids: return
        report_id = random.choice(self.known_report_ids)
        self.client.post(f"/api/v1/reports/{report_id}/endorse", headers=self.headers, name="/api/v1/reports/{id}/endorse")


class SupervisorBot(HttpUser):
    weight = 3
    wait_time = between(2, 5)
    host = "http://localhost:8080" 

    def on_start(self):
        # Log in as the mock supervisor
        response = self.client.post("/api/v1/auth/login", json={
            "email": "supervisor@test.com",
            "password": "Password123!"
        })

        if response.status_code == 200:
            token = response.json().get("data", {}).get("token")
            self.headers = {"Authorization": f"Bearer {token}"}
        else:
            self.headers = {}
            
        self.my_assigned_reports = []

    @task(3)
    def find_and_self_assign(self):
        """Look for PENDING reports and self-assign them."""
        if not getattr(self, 'headers', None): return
        lat = -12.0464 + random.uniform(-0.02, 0.02)
        lng = -77.0428 + random.uniform(-0.02, 0.02)
        
        response = self.client.get(
            f"/api/v1/reports/nearby?latitude={lat}&longitude={lng}&radius=10000", 
            headers=self.headers,
            name="/api/v1/reports/nearby (Supervisor)"
        )
        if response.status_code == 200:
            reports = response.json().get("data", [])
            pending_reports = [r for r in reports if r.get("status") == "PENDING"]
            if pending_reports:
                report = random.choice(pending_reports)
                report_id = report.get("id")
                assign_resp = self.client.put(f"/api/v1/reports/{report_id}/self-assign", headers=self.headers, name="/api/v1/reports/{id}/self-assign")
                if assign_resp.status_code == 200:
                    self.my_assigned_reports.append(report_id)

    @task(2)
    def resolve_reports(self):
        """Update assigned reports to RESOLVED."""
        if not getattr(self, 'headers', None) or not self.my_assigned_reports: return
        report_id = random.choice(self.my_assigned_reports)
        self.client.put(f"/api/v1/reports/{report_id}/status", json={"status": "RESOLVED"}, headers=self.headers, name="/api/v1/reports/{id}/status")
        
    @task(1)
    def optimize_route(self):
        """Request route optimization for a category."""
        if not getattr(self, 'headers', None): return
        lat = -12.0464 + random.uniform(-0.02, 0.02)
        lng = -77.0428 + random.uniform(-0.02, 0.02)
        self.client.post(f"/api/v1/reports/optimize-route?categoryId=1&latitude={lat}&longitude={lng}&radius=5000", headers=self.headers, name="/api/v1/reports/optimize-route")


class AdminBot(HttpUser):
    weight = 1
    wait_time = between(5, 10)
    host = "http://localhost:8080" 

    def on_start(self):
        # Log in as the mock admin
        response = self.client.post("/api/v1/auth/login", json={
            "email": "admin@test.com",
            "password": "Password123!"
        })

        if response.status_code == 200:
            token = response.json().get("data", {}).get("token")
            self.headers = {"Authorization": f"Bearer {token}"}
        else:
            self.headers = {}

    @task(1)
    def view_dashboard_reports(self):
        """Simulate loading the admin dashboard reports."""
        if not getattr(self, 'headers', None): return
        self.client.get("/api/v1/reports", headers=self.headers, name="/api/v1/reports (Admin Dashboard)")

    @task(1)
    def view_dashboard_users(self):
        """Simulate loading the admin dashboard users."""
        if not getattr(self, 'headers', None): return
        self.client.get("/api/v1/auth/accounts", headers=self.headers, name="/api/v1/auth/accounts (Admin Dashboard)")
