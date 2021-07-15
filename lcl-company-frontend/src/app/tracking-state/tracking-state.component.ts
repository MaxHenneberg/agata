import {Component, OnInit} from '@angular/core';
import {TrackingStateDto} from "../../dataholder/TrackingStateDto";
import {TrackingServiceService} from "../tracking-service.service";
import {ActivatedRoute} from "@angular/router";

@Component({
  selector: 'app-tracking-state',
  templateUrl: './tracking-state.component.html',
  styleUrls: ['./tracking-state.component.css']
})
export class TrackingStateComponent implements OnInit {

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
      this.stateHistory = res;
      this.completedIdx = this.stateHistory.length;
      // for (let i = 0; i < this.stateHistory.length; i++) {
      //   this.content
      // }
    });
  }

}
