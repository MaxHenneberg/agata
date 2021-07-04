import {Injectable} from '@angular/core';
import {RequestSlotTO} from '../dataholder/RequestSlotTO';
import {Observable, Subject} from 'rxjs';
// @ts-ignore
import pendingApprovalsJson from '../mockResponses/pendingApprovals.json';

@Injectable({
  providedIn: 'root'
})
export class RequestSlotService {

  private pendingApprovalSubject: Subject<any>;

  constructor() {
    this.pendingApprovalSubject = new Subject<string>();
  }

  requestSlot(payload: RequestSlotTO): boolean {
    return true;
  }

  pollPendingApprovals() {
    console.log(pendingApprovalsJson);
    pendingApprovalsJson.forEach(e => this.pendingApprovalSubject.next(e));
  }

  approveRequest(id: string) {
    console.log(id);
  }

  onPendingApproval(): Observable<string> {
    return this.pendingApprovalSubject.asObservable();
  }
}
