import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ContainerFromShippinglineComponent } from './container-from-shippingline.component';

describe('ContainerFromShippinglineComponent', () => {
  let component: ContainerFromShippinglineComponent;
  let fixture: ComponentFixture<ContainerFromShippinglineComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ContainerFromShippinglineComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ContainerFromShippinglineComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
