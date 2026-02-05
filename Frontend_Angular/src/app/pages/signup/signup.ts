import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router,RouterModule } from '@angular/router';

@Component({
  selector: 'app-signup',
  imports: [RouterModule,FormsModule,CommonModule],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
  standalone:true
})
export class Signup {

  name='';
  email='';
  password='';
  loading=false;
  error='';

  constructor(private http:HttpClient , private router:Router){}

  signup() {
  console.log('SIGNUP FUNCTION CALLED');
  if (!this.name || !this.email || !this.password) {
    this.error = 'All fields are required';
    return;
  }
  this.loading = true;
  this.error = '';

  this.http.post('http://localhost:8080/api/auth/register',{
      name: this.name,
      email: this.email,
      password: this.password
    },{observe:'response',responseType:'text'}
  ).subscribe({
    next: () => {
      console.log('API SUCCESS');
      alert('Signup success');
      this.router.navigateByUrl('/login');
    },
    error: (err) => {
      console.error(err);
      this.loading = false;
      this.error = 'Sign Up Failed';
    }
  });
}
}
