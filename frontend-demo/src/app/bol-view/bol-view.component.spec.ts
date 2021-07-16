import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BolViewComponent } from './bol-view.component';

describe('BolViewComponent', () => {
  let component: BolViewComponent;
  let fixture: ComponentFixture<BolViewComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BolViewComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BolViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
