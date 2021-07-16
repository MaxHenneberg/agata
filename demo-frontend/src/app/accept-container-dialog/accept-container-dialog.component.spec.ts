import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AcceptContainerDialogComponent } from './accept-container-dialog.component';

describe('AcceptContainerDialogComponent', () => {
  let component: AcceptContainerDialogComponent;
  let fixture: ComponentFixture<AcceptContainerDialogComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AcceptContainerDialogComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AcceptContainerDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
