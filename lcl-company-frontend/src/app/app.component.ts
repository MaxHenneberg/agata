import {Component, Inject} from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {

  constructor(@Inject('PROFILE') public profile: string) {
  }

  title = 'lcl-company-frontend';

  showSideNav = false;

  toggleSideNav() {
    console.log('toggle');
    this.showSideNav = !this.showSideNav;
  }
}
