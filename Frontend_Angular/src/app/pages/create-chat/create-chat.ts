import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';

@Component({
  selector: 'app-create-chat',
  imports: [FormsModule,CommonModule,RouterModule],
  templateUrl: './create-chat.html',
  styleUrl: './create-chat.css',
})
export class CreateChat {
  projectType='';
  language='';
  framework='';
  databaseType='';
  outputType='';

  error='';

  constructor(private http:HttpClient,private router:Router){}

  createChat(){
    if(!this.projectType || !this.language || !this.framework || !this.databaseType || !this.outputType){
      this.error="All fields are required";
      return;
    }
    const token=localStorage.getItem('token');

    this.http.post<any>('http://localhost:8080/api/chats',{
      projectType:this.projectType,
      language:this.language,
      framework:this.framework,
      databaseType:this.databaseType,
      outputType:this.outputType
    },{
      headers:{
        Authorization:`Bearer ${token}`,
      }
    }).subscribe({next:(chat)=>{
      this.router.navigate(['/chat',chat.id]);
    },
     error:(err)=>{
      console.log(err);
      this.error="Failed to create chat";
     }});
  }

}
