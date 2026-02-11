import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { ChangeDetectorRef } from '@angular/core';


@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {
  name = '';
  email = '';
  password = '';
  loading = false;
  toastMessage = '';
  constructor(
    private http: HttpClient,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  signup() {
  console.log('SIGNUP FUNCTION CALLED');

  if (!this.name || !this.email || !this.password) {
    this.showToast('All fields are required');
    return;
  }

  this.loading = true;

  this.http.post(
    'http://localhost:8080/api/auth/register',
    {
      name: this.name,
      email: this.email,
      password: this.password
    },
    {
      observe: 'response',
      responseType: 'text'
    }
  ).subscribe({
    next: () => {
      this.loading = false;
      this.showToast('Account created successfully');

      setTimeout(() => {
        this.router.navigateByUrl('/login');
      }, 1500);
    },
    error: (err) => {
      this.loading = false;

      if (err.status === 409) {
        this.showToast('Account detected. Please login.');
      } else if (err.status === 400) {
        this.showToast('Invalid data submitted.');
      } else {
        this.showToast('Sign Up Failed. Please try again.');
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
