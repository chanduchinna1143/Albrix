import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateChat } from './create-chat';

describe('CreateChat', () => {
  let component: CreateChat;
  let fixture: ComponentFixture<CreateChat>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateChat]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateChat);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
