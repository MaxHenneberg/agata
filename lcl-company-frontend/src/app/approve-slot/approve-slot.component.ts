import {Component, OnInit} from '@angular/core';
import {RequestSlotService} from '../request-slot.service';

@Component({
  selector: 'app-approve-slot',
  templateUrl: './approve-slot.component.html',
  styleUrls: ['./approve-slot.component.css']
})
export class ApproveSlotComponent implements OnInit {

  pendingApprovals: any[];

  constructor(private requestSlotService: RequestSlotService) {
  }

  ngOnInit(): void {
    this.pendingApprovals = [];
    this.requestSlotService.onPendingApproval().subscribe(next => this.pendingApprovals.push(next));
    this.requestSlotService.pollPendingApprovals();
  }

  approve(id: string) {
    this.requestSlotService.approveRequest(id);
  }
}
