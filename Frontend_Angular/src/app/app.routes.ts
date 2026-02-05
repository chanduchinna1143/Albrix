import { Routes } from '@angular/router';

import { Landing } from './pages/landing/landing';
import { Login } from './pages/login/login';
import { Signup } from './pages/signup/signup';
import { Chat } from './pages/chat/chat';
import { AuthGuard } from './guards/auth.guard';
import { CreateChat } from './pages/create-chat/create-chat';

export const routes: Routes = [
    {path:'',component:Landing},
    {path:'login', component:Login},
    {path:'signup',component:Signup},
    {path:'chat',component:Chat,canActivate:[AuthGuard]},
    {path:'create-chat',component:CreateChat},
    {path:'chat/:id',component:Chat},
    {path:'**',redirectTo:''}

];
