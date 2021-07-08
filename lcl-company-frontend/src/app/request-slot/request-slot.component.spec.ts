import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { RequestSlotComponent } from './request-slot.component';

describe('RequestSlotComponent', () => {
  let component: RequestSlotComponent;
  let fixture: ComponentFixture<RequestSlotComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ RequestSlotComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(RequestSlotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
