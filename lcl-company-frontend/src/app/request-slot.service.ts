import {Injectable} from '@angular/core';
import {RequestSlotTO} from '../dataholder/RequestSlotTO';
import {Observable, Subject} from 'rxjs';
// @ts-ignore
import pendingApprovalsJson from '../mockResponses/pendingApprovals.json';
import {HttpBaseService} from './http-base.service';

@Injectable({
  providedIn: 'root'
})
export class RequestSlotService {

  private pendingApprovalSubject: Subject<any>;

  constructor(private httpClient: HttpBaseService) {
    this.pendingApprovalSubject = new Subject<string>();
  }

  requestSlot(payload: RequestSlotTO): boolean {
    this.httpClient.post('/lcl-assignments/proposals', payload).subscribe(resp => console.log(resp));
    return true;
  }

  pollPendingApprovals() {
    console.log(pendingApprovalsJson);
    // @ts-ignore
    this.httpClient.get('/lcl-assignments/proposals').subscribe(e => e.forEach(ele => this.pendingApprovalSubject.next(ele)));
  }

  approveRequest(id: string) {
    this.httpClient.post('/lcl-assignments/proposals/' + id + '/acceptance', null).subscribe(res => console.log(res));
  }

  onPendingApproval(): Observable<string> {
    return this.pendingApprovalSubject.asObservable();
  }
}
