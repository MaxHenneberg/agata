import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';

@Component({
  selector: 'app-scanner-dialog',
  templateUrl: './scanner-dialog.component.html',
  styleUrls: ['./scanner-dialog.component.css']
})
export class ScannerDialogComponent implements OnInit {

  @Output()
  onSuccessfulScan: EventEmitter<string> = new EventEmitter<string>();

  constructor() {
  }

  ngOnInit(): void {
  }

  handleSuccessfulScan(event: string) {
    console.log(event);
    this.onSuccessfulScan.emit(event);
  }

}
