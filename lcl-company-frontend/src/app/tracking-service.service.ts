import {Injectable} from '@angular/core';
import {HttpBaseService} from "./http-base.service";
import {Observable} from "rxjs";
import {TrackingStateDto} from "../dataholder/TrackingStateDto";

@Injectable({
  providedIn: 'root'
})
export class TrackingServiceService {

  constructor(private httClient: HttpBaseService) {
  }

  getHistory(trackingStateId: string): Observable<TrackingStateDto[]> {
    return this.httClient.get('/tracking/' + trackingStateId);
  }
}
