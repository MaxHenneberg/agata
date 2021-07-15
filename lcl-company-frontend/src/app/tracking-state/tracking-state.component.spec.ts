import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TrackingStateComponent } from './tracking-state.component';

describe('TrackingStateComponent', () => {
  let component: TrackingStateComponent;
  let fixture: ComponentFixture<TrackingStateComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TrackingStateComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TrackingStateComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
