import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { RouterModule } from '@angular/router';

interface ChatMessage {
  role: 'USER' | 'AI';
  content: string;
}

@Component({
  selector: 'app-chat',
  standalone: true,
  imports: [CommonModule, FormsModule,RouterModule],
  templateUrl: './chat.html',
})
export class Chat {
  prompt = '';
  messages: ChatMessage[] = [];
  loading = false;
  chatId = 1; 
  constructor(private http: HttpClient) {}
  sendPrompt() {
    if (!this.prompt.trim()) return;
    const userMessage = this.prompt;
    this.messages.push({
      role: 'USER',
      content: userMessage
    });
    this.prompt = '';
    this.loading = true;
    this.http.post(`http://localhost:8080/api/chats/${this.chatId}/message`,
      { prompt: userMessage },
      { responseType: 'text' } 
    ).subscribe({
      next: (aiResponse) => {
        this.messages.push({
          role: 'AI',
          content: aiResponse
        });
        this.loading = false;
      },
      error: () => {
        this.messages.push({
          role: 'AI',
          content: 'Error getting response'
        });
        this.loading = false;
      }
    });
  }
}