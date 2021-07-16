import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {RequestSlotComponent} from './request-slot/request-slot.component';
import {ModifyPickupComponent} from './modify-pickup/modify-pickup.component';
import {BolCombinerComponent} from './bol-combiner/bol-combiner.component';
import {AcceptContainerComponent} from './accept-container/accept-container.component';
import {GoodsFromLclcompanyComponent} from './goods-from-lclcompany/goods-from-lclcompany.component';
import {TrackingStateComponent} from './tracking-state/tracking-state.component';


const routes: Routes = [
  {path: 'requestSlot', component: RequestSlotComponent},
  {path: 'modifyPickup/:id', component: ModifyPickupComponent},
  {path: 'bolCombiner', component: BolCombinerComponent},
  {path: 'acceptContainer', component: AcceptContainerComponent},
  {path: 'acceptGoods/:id', component: GoodsFromLclcompanyComponent},
  {path: 'trackingState/:id', component: TrackingStateComponent},
  {path: '', redirectTo: '/requestSlot', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
