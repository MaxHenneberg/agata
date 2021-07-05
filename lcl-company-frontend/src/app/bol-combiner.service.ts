import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
// @ts-ignore
import openContainer from '../mockResponses/openContainer.json';
import {HttpBaseService} from './http-base.service';
import {BolTO} from '../dataholder/BolTO';

@Injectable({
  providedIn: 'root'
})
export class BolCombinerService {

  constructor(private httpClient: HttpBaseService) {
  }

  pollOpenBol(): Observable<BolTO> {
    // @ts-ignore
    return this.httpClient.get('/pickups/bols');
  }
}
