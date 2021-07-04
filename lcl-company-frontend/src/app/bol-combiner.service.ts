import {Injectable} from '@angular/core';
import {Observable, Subject} from 'rxjs';
// @ts-ignore
import openContainer from '../mockResponses/openContainer.json';

@Injectable({
  providedIn: 'root'
})
export class BolCombinerService {

  private openContainerSubj: Subject<any>;

  constructor() {
    this.openContainerSubj = new Subject<any>();
  }

  getOpenContainerObserver(): Observable<any> {
    return this.openContainerSubj.asObservable();
  }

  pollOpenBol() {
    openContainer.forEach(e => this.openContainerSubj.next(e));
  }
}
