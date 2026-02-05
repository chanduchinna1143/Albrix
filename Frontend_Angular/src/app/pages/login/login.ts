import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-login',
  imports: [RouterModule,FormsModule,CommonModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {
  email='';
  password='';
  loading=false;
  error='';
  constructor(private http:HttpClient , private router:Router){}
  login(){
    if(!this.email || !this.password){
      this.error="All Fields are required";
      return;
    }
    this.loading=true;
    this.error='';
    this.http.post<any>('http://localhost:8080/api/auth/login',{
      email:this.email,
      password:this.password
    }).subscribe({
      next:(res)=>{
        localStorage.setItem('token',res.token);
        console.log('API SUCCESS');
        alert('Signup success');
        this.router.navigate(['/create-chat']);
      },error:(err)=>{
        console.log(err);
      this.error="Invalid credentials";
      this.loading=false;
    }})
  }
  


}
