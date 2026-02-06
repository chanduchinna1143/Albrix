import { ChangeDetectorRef, Component, ElementRef, OnDestroy, OnInit, ViewChild ,AfterViewChecked} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { ActivatedRoute, RouterModule } from '@angular/router';

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
export class Chat implements OnInit, OnDestroy {
  prompt = '';
  messages: ChatMessage[] = [];
  loading = false;
  chatId!: number;
  isTabActive = true;
  @ViewChild('bottom') bottom!: ElementRef;

  private typingInterval: any;
  private visibilityHandler = () => {
    this.isTabActive = !document.hidden;
  };
  constructor(
    private http: HttpClient,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute
  ) {
    document.addEventListener('visibilitychange', this.visibilityHandler);
  }
  ngOnInit() {
  this.chatId = Number(this.route.snapshot.paramMap.get('id'));

  const token = localStorage.getItem('token');
  if (!token) {
    console.error('No token found');
    return;
  }

  this.http.get<ChatMessage[]>(
    `http://localhost:8080/api/chats/${this.chatId}`,
    {
      headers: {
        Authorization: `Bearer ${token}`
      }
    }
  ).subscribe({
    next: (messages) => {
      this.messages = messages || [];
      this.cdr.detectChanges();
      this.scrollToBottom();
    },
    error: (err) => {
      console.error('Failed to load messages', err);
    }
  });
}


  typeText(fullText: string, messageRef: ChatMessage) {
    let index = 0;
    this.typingInterval = setInterval(() => {
      if (!this.isTabActive) {
        messageRef.content = fullText;
        clearInterval(this.typingInterval);
        this.loading = false;
        this.cdr.detectChanges();
        return;
      }

      messageRef.content += fullText.charAt(index);
      index++;
      this.cdr.detectChanges();

      if (index >= fullText.length) {
        clearInterval(this.typingInterval);
        this.loading = false;
        this.cdr.detectChanges();
      }

    }, );
  }

  sendPrompt() {
    if (!this.prompt.trim()) return;
    const token = localStorage.getItem('token');
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

    this.http.post(
      `http://localhost:8080/api/chats/${this.chatId}/message`,
      { prompt: userMessage },
      {
        headers: {
          Authorization: `Bearer ${token}`
        },
        responseType: 'text'
      }
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

      scrollToBottom() {
      if (this.bottom) {
        this.bottom.nativeElement.scrollIntoView({
          behavior: 'smooth'
        });
      }
    }


  ngOnDestroy() {
    document.removeEventListener('visibilitychange', this.visibilityHandler);
    if (this.typingInterval) {
      clearInterval(this.typingInterval);
    }
  }
}
