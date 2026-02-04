import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router,RouterModule } from '@angular/router';

@Component({
  selector: 'app-signup',
  imports: [RouterModule,FormsModule],
  templateUrl: './signup.html',
  styleUrl: './signup.css',
})
export class Signup {

  name='';
  email='';
  password='';
  loading=false;
  error='';

  constructor(private http:HttpClient , private router:Router){}

  signup(){
    if(!this.name || !this.email || !this.password){
      this.error="All fields are required";
      return;
    }
    this.loading=true;
    this.error='';
    this.http.post('http://localhost:8080/api/auth/register',{name:this.name,email:this.email,password:this.password})
    .subscribe({next:()=>{this.router.navigate(['/login']);},
    error:()=>{this.error="Sign Up Failed";this.loading=false;}});
  }
}
