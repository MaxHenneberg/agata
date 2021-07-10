import {Component, Input, OnInit} from '@angular/core';

@Component({
  selector: 'app-custom-stepper',
  templateUrl: './custom-stepper.component.html',
  styleUrls: ['./custom-stepper.component.css']
})
export class CustomStepperComponent implements OnInit {

  steps = ['Slot Booked', 'Pickup Completed', 'Loaded On Ship', 'Deconsolidated', 'Goods Delivered'];

  @Input()
  completedIndex: number;

  @Input()
  content: { label: string, value: string }[][];

  constructor() {
  }

  ngOnInit(): void {
    console.log(this.content);
    console.log(this.completedIndex);
  }

  getContent(idx: number) {
    if (this.content && this.content[idx]) {
      return this.content[idx];
    } else {
      return [];
    }
  }

}
