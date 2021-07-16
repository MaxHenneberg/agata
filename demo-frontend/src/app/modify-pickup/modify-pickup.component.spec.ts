import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ModifyPickupComponent } from './modify-pickup.component';

describe('ModifyPickupComponent', () => {
  let component: ModifyPickupComponent;
  let fixture: ComponentFixture<ModifyPickupComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ModifyPickupComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ModifyPickupComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
