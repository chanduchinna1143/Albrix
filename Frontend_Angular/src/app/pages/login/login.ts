import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email = '';
  password = '';
  loading = false;
  toastMessage = '';
  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  login() {
    if (!this.email || !this.password) {
      this.showToast('Email and password are required');
      return;
    }

    this.loading = true;

    this.http.post<any>(
      'http://localhost:8080/api/auth/login',
      {
        email: this.email,
        password: this.password
      }
    ).subscribe({
      next: (res) => {
        this.loading = false;
        localStorage.setItem('token', res.token);
        this.showToast('Login successful');
        setTimeout(() => {
          this.router.navigate(['/chat']);
        }, 1000);
      },
      error: (err) => {
        this.loading = false;
        console.error(err);
        if (err.status === 404) {
          this.showToast('Email not found');
        } else if (err.status === 401 || err.status === 403) {
          this.showToast('Password is incorrect');
        } else {
          this.showToast('Login failed. Please try again.');
        }
      }
    });
  }
  showToast(message: string) {
    this.toastMessage = message;
    this.cdr.detectChanges();

    setTimeout(() => {
      this.toastMessage = '';
      this.cdr.detectChanges();
    }, 3000);
  }
}
