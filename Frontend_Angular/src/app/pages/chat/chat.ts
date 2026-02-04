import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-chat',
  imports: [RouterModule,FormsModule,CommonModule],
  templateUrl: './chat.html',
  styleUrl: './chat.css',
})
export class Chat {
  prompt = '';
  loading=false;
  messages:{role:'USER'|'AI';content:string}[]=[];
  constructor(private http:HttpClient){}
  sendPrompt(){
    if (!this.prompt.trim()) return;
    const userMessage=this.prompt;
    this.prompt='';
    this.loading=true;
    this.messages.push({role:'USER',content:userMessage});
    const token=localStorage.getItem('token');
    this.http.post('http://localhost:8080/api/chats/1/message', { prompt : userMessage},{headers:{Authorization :`Bearer ${token}`},responseType:'text'})
    .subscribe({next:(res)=>{this.messages.push({role:'AI',content:res});this.loading=false;},error:()=>{this.messages.push({role:'AI',content:'Error generating the response'});
    this.loading=false;
  }})
  }

}
