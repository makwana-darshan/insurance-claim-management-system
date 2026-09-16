import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  steps = [
    { number: '01', title: 'Sign Up', description: 'Create a free account in under a minute.' },
    {
      number: '02',
      title: 'File a Claim',
      description: 'Fill in your policy details and submit for review.',
    },
    {
      number: '03',
      title: 'Get Assigned',
      description: 'A claim officer reviews and assigns a surveyor.',
    },
    {
      number: '04',
      title: 'Track & Resolve',
      description: 'Follow every status update until a final decision.',
    },
  ];

  roles = [
    {
      icon: '👤',
      name: 'Customer',
      description: 'File claims and track their progress in real time.',
    },
    { icon: '📋', name: 'Claim Officer', description: 'Review submissions and assign surveyors.' },
    { icon: '🔍', name: 'Surveyor', description: 'Inspect claims and report findings.' },
    { icon: '✅', name: 'Admin', description: 'Approve or reject claims with full audit history.' },
  ];

  constructor(private titleService: Title) {
    this.titleService.setTitle('ICMS - Insurance Claim Management System');
  }
}
