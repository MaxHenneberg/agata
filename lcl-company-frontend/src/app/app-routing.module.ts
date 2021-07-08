import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterModule, Routes} from '@angular/router';
import {RequestSlotComponent} from './request-slot/request-slot.component';
import {ApproveSlotComponent} from './approve-slot/approve-slot.component';
import {RequestContainerComponent} from './request-container/request-container.component';
import {AddGoodsComponent} from './add-goods/add-goods.component';
import {ModifyPickupComponent} from './modify-pickup/modify-pickup.component';
import {BolCombinerComponent} from './bol-combiner/bol-combiner.component';
import {AcceptContainerComponent} from './accept-container/accept-container.component';
import {GoodsFromLclcompanyComponent} from './goods-from-lclcompany/goods-from-lclcompany.component';



const routes: Routes = [
  { path: 'requestSlot', component: RequestSlotComponent },
  { path: 'approveSlot', component: ApproveSlotComponent },
  { path: 'requestContainer', component: RequestContainerComponent },
  { path: 'modifyPickup/:id', component: ModifyPickupComponent },
  { path: 'bolCombiner', component: BolCombinerComponent },
  { path: 'acceptContainer', component: AcceptContainerComponent },
  { path: 'acceptGoods/:id', component: GoodsFromLclcompanyComponent },
  { path: '', redirectTo: '/requestSlot', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
