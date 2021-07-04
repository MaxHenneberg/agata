import {Component, OnInit} from '@angular/core';
import {BolCombinerService} from '../bol-combiner.service';

@Component({
  selector: 'app-bol-combiner',
  templateUrl: './bol-combiner.component.html',
  styleUrls: ['./bol-combiner.component.css']
})
export class BolCombinerComponent implements OnInit {

  columnsToDisplay = ['containerId', 'type', '#bol', 'fill%', 'buttonCol'];
  dataSource: any[];

  constructor(private bolCombinerService: BolCombinerService) {
  }

  ngOnInit(): void {
    this.dataSource = [];
    this.bolCombinerService.getOpenContainerObserver().subscribe(e => {
      console.log(e);
      this.dataSource.push(e);
    });
    this.bolCombinerService.pollOpenBol();
  }

}
