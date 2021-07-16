import { TestBed } from '@angular/core/testing';

import { TrackingServiceService } from './tracking-service.service';

describe('TrackingServiceService', () => {
  let service: TrackingServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(TrackingServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
