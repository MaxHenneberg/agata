import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AcceptContainerComponent } from './accept-container.component';
// @ts-ignore
import {MatSnackBar, MatSnackBarModule} from '@angular/material';

describe('AcceptContainerComponent', () => {
  let component: AcceptContainerComponent;
  let fixture: ComponentFixture<AcceptContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AcceptContainerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AcceptContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
