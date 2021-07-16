export class ItemRow {
  mark: string;
  identityNumber: string;
  noOfPackages: number;
  descriptionOfGoods: {
    product: string;
    typeOfPackage: string;
    quantity: number;
  };
  netWeight: number;
  grossWeight: number;
  measurement: number;
}
