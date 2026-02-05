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
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './chat.html',
})
export class Chat {
  prompt = '';
  messages: ChatMessage[] = [];
  loading = false;
  chatId = 1;
  constructor(private http: HttpClient) {}
  typeText(fullText: string, messageRef: ChatMessage) {
    let index = 0;

    const interval = setInterval(() => {
      messageRef.content += fullText.charAt(index);
      index++;

      if (index >= fullText.length) {
        clearInterval(interval);
        this.loading = false;
      }
    }, 20);
  }
  sendPrompt() {
    if (!this.prompt.trim()) return;
    this.messages.push({
      role: 'USER',
      content: this.prompt
    });
    const userMessage = this.prompt;
    this.prompt = '';
    const aiMessage: ChatMessage = {
      role: 'AI',
      content: ''
    };
    this.messages.push(aiMessage);
    this.loading = true;
    this.http.post(`http://localhost:8080/api/chats/${this.chatId}/message`,
      { prompt: userMessage },
      { responseType: 'text' }
    ).subscribe({
      next: (aiResponse: string) => {
        this.typeText(aiResponse, aiMessage);
      },
      error: () => {
        aiMessage.content = 'Error getting response';
        this.loading = false;
      }
    });
  }
}