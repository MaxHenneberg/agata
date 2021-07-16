import {Injectable} from '@angular/core';
import {Observable} from 'rxjs';
import {HttpBaseService} from './http-base.service';
import {BolTO} from '../dataholder/BolTO';

@Injectable({
  providedIn: 'root'
})
export class BolCombinerService {

  constructor(private httpClient: HttpBaseService) {
  }

  pollOpenBol(): Observable<BolTO> {
    return this.httpClient.get('/bill-of-ladings?type=House');
  }
}
