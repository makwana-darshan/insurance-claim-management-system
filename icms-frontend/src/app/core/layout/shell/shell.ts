import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet],
  templateUrl: './shell.html',
  styleUrl: './shell.css'
})
export class Shell {

  constructor(public authService: AuthService) {}

  logout(): void {
    this.authService.logout();
  }
}