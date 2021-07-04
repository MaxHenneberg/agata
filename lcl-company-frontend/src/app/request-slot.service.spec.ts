import { TestBed } from '@angular/core/testing';

import { RequestSlotService } from './request-slot.service';

describe('RequestSlotService', () => {
  let service: RequestSlotService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(RequestSlotService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
