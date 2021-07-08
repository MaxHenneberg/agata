import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BolCombinerComponent } from './bol-combiner.component';

describe('BolCombinerComponent', () => {
  let component: BolCombinerComponent;
  let fixture: ComponentFixture<BolCombinerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BolCombinerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BolCombinerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
