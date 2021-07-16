import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApproveSlotComponent } from './approve-slot.component';

describe('ApproveSlotComponent', () => {
  let component: ApproveSlotComponent;
  let fixture: ComponentFixture<ApproveSlotComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApproveSlotComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApproveSlotComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
