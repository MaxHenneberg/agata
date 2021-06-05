package agata.lcl.states.pickup;

import agata.lcl.contracts.pickup.PickupContract;
import agata.lcl.states.Proposal;
import net.corda.core.contracts.BelongsToContract;
import net.corda.core.contracts.CommandData;
import net.corda.core.contracts.UniqueIdentifier;
import net.corda.core.identity.Party;
import net.corda.core.serialization.ConstructorForDeserialization;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@BelongsToContract(PickupContract.class)
public class PickupProposal extends PickupState implements Proposal<PickupState> {
    private final Party proposee;
    private final Party proposer;

    private final CommandData command;

    @ConstructorForDeserialization
    public PickupProposal(Party proposer, Party proposee, CommandData command,Party exporter, Party supplier, Party lclCompany, List<String> pickedUpGoods, UniqueIdentifier referenceToState1, UniqueIdentifier linearId) {
        super(exporter, supplier, lclCompany, pickedUpGoods, referenceToState1, linearId);
        this.proposee = proposee;
        this.proposer = proposer;
        this.command = command;
    }

    public PickupProposal(Party proposer, Party proposee, CommandData command,Party exporter, Party supplier, Party lclCompany, List<String> pickedUpGoods, UniqueIdentifier referenceToState1) {
        super(exporter, supplier, lclCompany, pickedUpGoods, referenceToState1);
        this.proposee = proposee;
        this.proposer = proposer;
        this.command = command;
    }

    public PickupProposal(Party proposer, Party proposee, CommandData command, PickupState proposal){
        super(proposal.getExporter(), proposal.getSupplier(), proposal.getLclCompany(), proposal.pickedUpGoods, proposal.getReferenceToState1());
        this.proposee = proposee;
        this.proposer = proposer;
        this.command = command;
    }

    public PickupProposal(Party proposer, Party proposee, CommandData command, PickupState proposal, UniqueIdentifier linearId){
        super(proposal.getExporter(), proposal.getSupplier(), proposal.getLclCompany(), proposal.pickedUpGoods, proposal.getReferenceToState1(), linearId);
        this.proposee = proposee;
        this.proposer = proposer;
        this.command = command;
    }

    public Party getExporter() {
        return exporter;
    }

    public Party getSupplier() {
        return supplier;
    }

    public Party getLclCompany() {
        return lclCompany;
    }

    public List<String> getPickedUpGoods() {
        return pickedUpGoods;
    }

    public UniqueIdentifier getReferenceToState1() {
        return referenceToState1;
    }

    @NotNull
    @Override
    public UniqueIdentifier getLinearId() {
        return this.linearId;
    }

    @Override
    public Party getProposee() {
        return this.proposee;
    }

    @Override
    public Party getProposer() {
        return this.proposer;
    }

    @Override
    public PickupState getProposedState() {
        return new PickupState(this.exporter, this.supplier, this.lclCompany, this.pickedUpGoods, this.referenceToState1);
    }

    @Override
    public CommandData getProposalCommand() {
        return this.command;
    }

    @Override
    public boolean isContractionalEqual(PickupState other) {
        return this.exporter.equals(other.exporter)
                && this.supplier.equals(other.supplier)
                && this.lclCompany.equals(other.lclCompany)
                && this.pickedUpGoods.equals(other.pickedUpGoods)
                && this.referenceToState1.equals(other.referenceToState1);
    }
}
