import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { GoodsFromLclcompanyComponent } from './goods-from-lclcompany.component';

describe('GoodsFromLclcompanyComponent', () => {
  let component: GoodsFromLclcompanyComponent;
  let fixture: ComponentFixture<GoodsFromLclcompanyComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ GoodsFromLclcompanyComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(GoodsFromLclcompanyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
