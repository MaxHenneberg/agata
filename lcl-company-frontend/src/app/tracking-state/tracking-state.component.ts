import {Component, OnInit} from '@angular/core';
import {TrackingStateDto} from '../../dataholder/TrackingStateDto';
import {TrackingServiceService} from '../tracking-service.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-tracking-state',
  templateUrl: './tracking-state.component.html',
  styleUrls: ['./tracking-state.component.css']
})
export class TrackingStateComponent implements OnInit {

  mapper = {
    updatedOn: 'Updated On: ',
    lclCompany: 'LcL-Company: ',
    buyer: 'Buyer: ',
    supplier: 'Supplier: ',
    shippingLine: 'Shipping Line: ',
    lastPort: 'Port: '
  };

  stateHistory: TrackingStateDto[];
  content: { label: string, value: string }[][];
  trackingStateId: string;
  completedIdx: number;

  constructor(private trackingService: TrackingServiceService, private route: ActivatedRoute) {
    this.stateHistory = [];
    this.completedIdx = -1;
    this.content = [[], [], [], [], []];
    this.trackingStateId = this.route.snapshot.paramMap.get('id');
  }

  ngOnInit(): void {
    this.trackingService.getHistory(this.trackingStateId).subscribe(res => {
    this.completedIdx = this.stateHistory.length;
    for (let i = 0; i < this.stateHistory.length; i++) {
      const curContent = [];
      for (let [key, value] of Object.entries(this.stateHistory[i])) {
        if (value && key !== 'status') {
          if (key === 'updatedOn') {
            const f = new Intl.DateTimeFormat('en');
            value = f.format(value);
          }
          console.log(this.mapper);
          curContent.push({label: this.mapper[key], value});
        }
      }
      this.content[i] = curContent;
    }
    });
  }

}
