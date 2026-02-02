package com.rarilabs.celestial.contracts.rarimo;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Array;
import org.web3j.abi.datatypes.Bool;
// CustomError not available in this web3j version
// import org.web3j.abi.datatypes.CustomError;
import org.web3j.abi.datatypes.DynamicArray;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint64;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.BaseEventResponse;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/LFDT-web3j/web3j/tree/main/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 1.7.0.
 */
@SuppressWarnings("rawtypes")
public class StateKeeper extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_REVOKED = "REVOKED";

    public static final String FUNC_UPGRADE_INTERFACE_VERSION = "UPGRADE_INTERFACE_VERSION";

    public static final String FUNC_USED = "USED";

    public static final String FUNC___STATEKEEPER_INIT = "__StateKeeper_init";

    public static final String FUNC___STATEKEEPER_INIT_V2 = "__StateKeeper_init_v2";

    public static final String FUNC_ADDBOND = "addBond";

    public static final String FUNC_ADDCERTIFICATE = "addCertificate";

    public static final String FUNC_ADDOWNERS = "addOwners";

    public static final String FUNC_CERTIFICATESSMT = "certificatesSmt";

    public static final String FUNC_CHANGEICAOMASTERTREEROOT = "changeICAOMasterTreeRoot";

    public static final String FUNC_GETCERTIFICATEINFO = "getCertificateInfo";

    public static final String FUNC_GETOWNERS = "getOwners";

    public static final String FUNC_GETPASSPORTINFO = "getPassportInfo";

    public static final String FUNC_GETPASSPORTSESSIONS = "getPassportSessions";

    public static final String FUNC_GETPASSPORTSESSIONSINFO = "getPassportSessionsInfo";

    public static final String FUNC_GETREGISTRATIONBYKEY = "getRegistrationByKey";

    public static final String FUNC_GETREGISTRATIONS = "getRegistrations";

    public static final String FUNC_GETSESSIONINFO = "getSessionInfo";

    public static final String FUNC_ICAOMASTERTREEMERKLEROOT = "icaoMasterTreeMerkleRoot";

    public static final String FUNC_IMPLEMENTATION = "implementation";

    public static final String FUNC_ISOWNER = "isOwner";

    public static final String FUNC_ISPASSPORTFULLYREVOKED = "isPassportFullyRevoked";

    public static final String FUNC_ISREGISTRATION = "isRegistration";

    public static final String FUNC_PROXIABLEUUID = "proxiableUUID";

    public static final String FUNC_REMOVECERTIFICATE = "removeCertificate";

    public static final String FUNC_REMOVEOWNERS = "removeOwners";

    public static final String FUNC_RENOUNCEOWNERSHIP = "renounceOwnership";

    public static final String FUNC_REVOKEALLBONDS = "revokeAllBonds";

    public static final String FUNC_REVOKEBOND = "revokeBond";

    public static final String FUNC_REVOKEBONDS = "revokeBonds";

    public static final String FUNC_REVOKEBONDSEXCEPT = "revokeBondsExcept";

    public static final String FUNC_UPDATEREGISTRATIONSET = "updateRegistrationSet";

    public static final String FUNC_UPGRADETOANDCALL = "upgradeToAndCall";

    public static final String FUNC_USESIGNATURE = "useSignature";

    public static final String FUNC_USEDSIGNATURES = "usedSignatures";

    // CustomError not available in this web3j version - commented out
    /*
    public static final CustomError ADDRESSEMPTYCODE_ERROR = new CustomError("AddressEmptyCode",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final CustomError ERC1967INVALIDIMPLEMENTATION_ERROR = new CustomError("ERC1967InvalidImplementation",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;

    public static final CustomError ERC1967NONPAYABLE_ERROR = new CustomError("ERC1967NonPayable",
            Arrays.<TypeReference<?>>asList());
    ;

    public static final CustomError FAILEDCALL_ERROR = new CustomError("FailedCall",
            Arrays.<TypeReference<?>>asList());
    ;

    public static final CustomError INVALIDINITIALIZATION_ERROR = new CustomError("InvalidInitialization",
            Arrays.<TypeReference<?>>asList());
    ;

    public static final CustomError INVALIDOWNER_ERROR = new CustomError("InvalidOwner",
            Arrays.<TypeReference<?>>asList());
    ;

    public static final CustomError NOTINITIALIZING_ERROR = new CustomError("NotInitializing",
            Arrays.<TypeReference<?>>asList());
    ;

    public static final CustomError UUPSUNAUTHORIZEDCALLCONTEXT_ERROR = new CustomError("UUPSUnauthorizedCallContext",
            Arrays.<TypeReference<?>>asList());
    ;

    public static final CustomError UUPSUNSUPPORTEDPROXIABLEUUID_ERROR = new CustomError("UUPSUnsupportedProxiableUUID",
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
    ;

    public static final CustomError UNAUTHORIZEDACCOUNT_ERROR = new CustomError("UnauthorizedAccount",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
    ;
    */

    public static final Event BONDADDED_EVENT = new Event("BondAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}));
    ;

    public static final Event BONDREVOKED_EVENT = new Event("BondRevoked", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}));
    ;

    public static final Event BONDSESSIONREISSUED_EVENT = new Event("BondSessionReissued", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Bytes32>() {}));
    ;

    public static final Event CERTIFICATEADDED_EVENT = new Event("CertificateAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}, new TypeReference<Uint256>() {}));
    ;

    public static final Event CERTIFICATEREMOVED_EVENT = new Event("CertificateRemoved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
    ;

    public static final Event INITIALIZED_EVENT = new Event("Initialized", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}));
    ;

    public static final Event OWNERSADDED_EVENT = new Event("OwnersAdded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
    ;

    public static final Event OWNERSREMOVED_EVENT = new Event("OwnersRemoved", 
            Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
    ;

    public static final Event UPGRADED_EVENT = new Event("Upgraded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected StateKeeper(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected StateKeeper(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected StateKeeper(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected StateKeeper(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static List<BondAddedEventResponse> getBondAddedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BONDADDED_EVENT, transactionReceipt);
        ArrayList<BondAddedEventResponse> responses = new ArrayList<BondAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BondAddedEventResponse typedResponse = new BondAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.sessionKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BondAddedEventResponse getBondAddedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BONDADDED_EVENT, log);
        BondAddedEventResponse typedResponse = new BondAddedEventResponse();
        typedResponse.log = log;
        typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.sessionKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<BondAddedEventResponse> bondAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBondAddedEventFromLog(log));
    }

    public Flowable<BondAddedEventResponse> bondAddedEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BONDADDED_EVENT));
        return bondAddedEventFlowable(filter);
    }

    public static List<BondRevokedEventResponse> getBondRevokedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BONDREVOKED_EVENT, transactionReceipt);
        ArrayList<BondRevokedEventResponse> responses = new ArrayList<BondRevokedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BondRevokedEventResponse typedResponse = new BondRevokedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.sessionKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BondRevokedEventResponse getBondRevokedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BONDREVOKED_EVENT, log);
        BondRevokedEventResponse typedResponse = new BondRevokedEventResponse();
        typedResponse.log = log;
        typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.sessionKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<BondRevokedEventResponse> bondRevokedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBondRevokedEventFromLog(log));
    }

    public Flowable<BondRevokedEventResponse> bondRevokedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BONDREVOKED_EVENT));
        return bondRevokedEventFlowable(filter);
    }

    public static List<BondSessionReissuedEventResponse> getBondSessionReissuedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(BONDSESSIONREISSUED_EVENT, transactionReceipt);
        ArrayList<BondSessionReissuedEventResponse> responses = new ArrayList<BondSessionReissuedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            BondSessionReissuedEventResponse typedResponse = new BondSessionReissuedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.sessionKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static BondSessionReissuedEventResponse getBondSessionReissuedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(BONDSESSIONREISSUED_EVENT, log);
        BondSessionReissuedEventResponse typedResponse = new BondSessionReissuedEventResponse();
        typedResponse.log = log;
        typedResponse.passportKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.sessionKey = (byte[]) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<BondSessionReissuedEventResponse> bondSessionReissuedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getBondSessionReissuedEventFromLog(log));
    }

    public Flowable<BondSessionReissuedEventResponse> bondSessionReissuedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(BONDSESSIONREISSUED_EVENT));
        return bondSessionReissuedEventFlowable(filter);
    }

    public static List<CertificateAddedEventResponse> getCertificateAddedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CERTIFICATEADDED_EVENT, transactionReceipt);
        ArrayList<CertificateAddedEventResponse> responses = new ArrayList<CertificateAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CertificateAddedEventResponse typedResponse = new CertificateAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.certificateKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse.expirationTimestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CertificateAddedEventResponse getCertificateAddedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CERTIFICATEADDED_EVENT, log);
        CertificateAddedEventResponse typedResponse = new CertificateAddedEventResponse();
        typedResponse.log = log;
        typedResponse.certificateKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        typedResponse.expirationTimestamp = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
        return typedResponse;
    }

    public Flowable<CertificateAddedEventResponse> certificateAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCertificateAddedEventFromLog(log));
    }

    public Flowable<CertificateAddedEventResponse> certificateAddedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CERTIFICATEADDED_EVENT));
        return certificateAddedEventFlowable(filter);
    }

    public static List<CertificateRemovedEventResponse> getCertificateRemovedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(CERTIFICATEREMOVED_EVENT, transactionReceipt);
        ArrayList<CertificateRemovedEventResponse> responses = new ArrayList<CertificateRemovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            CertificateRemovedEventResponse typedResponse = new CertificateRemovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.certificateKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static CertificateRemovedEventResponse getCertificateRemovedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(CERTIFICATEREMOVED_EVENT, log);
        CertificateRemovedEventResponse typedResponse = new CertificateRemovedEventResponse();
        typedResponse.log = log;
        typedResponse.certificateKey = (byte[]) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<CertificateRemovedEventResponse> certificateRemovedEventFlowable(
            EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getCertificateRemovedEventFromLog(log));
    }

    public Flowable<CertificateRemovedEventResponse> certificateRemovedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(CERTIFICATEREMOVED_EVENT));
        return certificateRemovedEventFlowable(filter);
    }

    public static List<InitializedEventResponse> getInitializedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(INITIALIZED_EVENT, transactionReceipt);
        ArrayList<InitializedEventResponse> responses = new ArrayList<InitializedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            InitializedEventResponse typedResponse = new InitializedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static InitializedEventResponse getInitializedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(INITIALIZED_EVENT, log);
        InitializedEventResponse typedResponse = new InitializedEventResponse();
        typedResponse.log = log;
        typedResponse.version = (BigInteger) eventValues.getNonIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<InitializedEventResponse> initializedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getInitializedEventFromLog(log));
    }

    public Flowable<InitializedEventResponse> initializedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(INITIALIZED_EVENT));
        return initializedEventFlowable(filter);
    }

    public static List<OwnersAddedEventResponse> getOwnersAddedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERSADDED_EVENT, transactionReceipt);
        ArrayList<OwnersAddedEventResponse> responses = new ArrayList<OwnersAddedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnersAddedEventResponse typedResponse = new OwnersAddedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.newOwners = (List<String>) ((Array) eventValues.getNonIndexedValues().get(0)).getNativeValueCopy();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnersAddedEventResponse getOwnersAddedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERSADDED_EVENT, log);
        OwnersAddedEventResponse typedResponse = new OwnersAddedEventResponse();
        typedResponse.log = log;
        typedResponse.newOwners = (List<String>) ((Array) eventValues.getNonIndexedValues().get(0)).getNativeValueCopy();
        return typedResponse;
    }

    public Flowable<OwnersAddedEventResponse> ownersAddedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnersAddedEventFromLog(log));
    }

    public Flowable<OwnersAddedEventResponse> ownersAddedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSADDED_EVENT));
        return ownersAddedEventFlowable(filter);
    }

    public static List<OwnersRemovedEventResponse> getOwnersRemovedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(OWNERSREMOVED_EVENT, transactionReceipt);
        ArrayList<OwnersRemovedEventResponse> responses = new ArrayList<OwnersRemovedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            OwnersRemovedEventResponse typedResponse = new OwnersRemovedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.removedOwners = (List<String>) ((Array) eventValues.getNonIndexedValues().get(0)).getNativeValueCopy();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static OwnersRemovedEventResponse getOwnersRemovedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(OWNERSREMOVED_EVENT, log);
        OwnersRemovedEventResponse typedResponse = new OwnersRemovedEventResponse();
        typedResponse.log = log;
        typedResponse.removedOwners = (List<String>) ((Array) eventValues.getNonIndexedValues().get(0)).getNativeValueCopy();
        return typedResponse;
    }

    public Flowable<OwnersRemovedEventResponse> ownersRemovedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getOwnersRemovedEventFromLog(log));
    }

    public Flowable<OwnersRemovedEventResponse> ownersRemovedEventFlowable(
            DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(OWNERSREMOVED_EVENT));
        return ownersRemovedEventFlowable(filter);
    }

    public static List<UpgradedEventResponse> getUpgradedEvents(
            TransactionReceipt transactionReceipt) {
        List<Contract.EventValuesWithLog> valueList = staticExtractEventParametersWithLog(UPGRADED_EVENT, transactionReceipt);
        ArrayList<UpgradedEventResponse> responses = new ArrayList<UpgradedEventResponse>(valueList.size());
        for (Contract.EventValuesWithLog eventValues : valueList) {
            UpgradedEventResponse typedResponse = new UpgradedEventResponse();
            typedResponse.log = eventValues.getLog();
            typedResponse.implementation = (String) eventValues.getIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public static UpgradedEventResponse getUpgradedEventFromLog(Log log) {
        Contract.EventValuesWithLog eventValues = staticExtractEventParametersWithLog(UPGRADED_EVENT, log);
        UpgradedEventResponse typedResponse = new UpgradedEventResponse();
        typedResponse.log = log;
        typedResponse.implementation = (String) eventValues.getIndexedValues().get(0).getValue();
        return typedResponse;
    }

    public Flowable<UpgradedEventResponse> upgradedEventFlowable(EthFilter filter) {
        return web3j.ethLogFlowable(filter).map(log -> getUpgradedEventFromLog(log));
    }

    public Flowable<UpgradedEventResponse> upgradedEventFlowable(DefaultBlockParameter startBlock,
            DefaultBlockParameter endBlock) {
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(UPGRADED_EVENT));
        return upgradedEventFlowable(filter);
    }

    public RemoteFunctionCall<byte[]> REVOKED() {
        final Function function = new Function(FUNC_REVOKED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<String> UPGRADE_INTERFACE_VERSION() {
        final Function function = new Function(FUNC_UPGRADE_INTERFACE_VERSION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<byte[]> USED() {
        final Function function = new Function(FUNC_USED, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> __StateKeeper_init(String initialOwner_,
            String certificatesSmt_, byte[] icaoMasterTreeMerkleRoot_) {
        final Function function = new Function(
                FUNC___STATEKEEPER_INIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, initialOwner_), 
                new org.web3j.abi.datatypes.Address(160, certificatesSmt_), 
                new org.web3j.abi.datatypes.generated.Bytes32(icaoMasterTreeMerkleRoot_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> __StateKeeper_init_v2() {
        final Function function = new Function(
                FUNC___STATEKEEPER_INIT_V2, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addBond(byte[] passportKey_, byte[] passportHash_,
            byte[] sessionKey_) {
        final Function function = new Function(
                FUNC_ADDBOND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_), 
                new org.web3j.abi.datatypes.generated.Bytes32(passportHash_), 
                new org.web3j.abi.datatypes.generated.Bytes32(sessionKey_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addCertificate(byte[] certificateKey_,
            BigInteger expirationTimestamp_) {
        final Function function = new Function(
                FUNC_ADDCERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(certificateKey_), 
                new org.web3j.abi.datatypes.generated.Uint256(expirationTimestamp_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> addOwners(List<String> newOwners_) {
        final Function function = new Function(
                FUNC_ADDOWNERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(newOwners_, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> certificatesSmt() {
        final Function function = new Function(FUNC_CERTIFICATESSMT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> changeICAOMasterTreeRoot(byte[] newRoot_) {
        final Function function = new Function(
                FUNC_CHANGEICAOMASTERTREEROOT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(newRoot_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<CertificateInfo> getCertificateInfo(byte[] certificateKey_) {
        final Function function = new Function(FUNC_GETCERTIFICATEINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(certificateKey_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<CertificateInfo>() {}));
        return executeRemoteCallSingleValueReturn(function, CertificateInfo.class);
    }

    public RemoteFunctionCall<List> getOwners() {
        final Function function = new Function(FUNC_GETOWNERS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<PassportInfo> getPassportInfo(byte[] passportKey_) {
        final Function function = new Function(FUNC_GETPASSPORTINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<PassportInfo>() {}));
        return executeRemoteCallSingleValueReturn(function, PassportInfo.class);
    }

    public RemoteFunctionCall<List> getPassportSessions(byte[] passportKey_) {
        final Function function = new Function(FUNC_GETPASSPORTSESSIONS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {}));
        return new RemoteFunctionCall<List>(function,
                new Callable<List>() {
                    @Override
                    @SuppressWarnings("unchecked")
                    public List call() throws Exception {
                        List<Type> result = (List<Type>) executeCallSingleValueReturn(function, List.class);
                        return convertToNative(result);
                    }
                });
    }

    public RemoteFunctionCall<Tuple2<List<byte[]>, List<SessionInfo>>> getPassportSessionsInfo(
            byte[] passportKey_) {
        final Function function = new Function(FUNC_GETPASSPORTSESSIONSINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Bytes32>>() {}, new TypeReference<DynamicArray<SessionInfo>>() {}));
        return new RemoteFunctionCall<Tuple2<List<byte[]>, List<SessionInfo>>>(function,
                new Callable<Tuple2<List<byte[]>, List<SessionInfo>>>() {
                    @Override
                    public Tuple2<List<byte[]>, List<SessionInfo>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<List<byte[]>, List<SessionInfo>>(
                                convertToNative((List<Bytes32>) results.get(0).getValue()), 
                                convertToNative((List<SessionInfo>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<String> getRegistrationByKey(String key_) {
        final Function function = new Function(FUNC_GETREGISTRATIONBYKEY, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(key_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Tuple2<List<String>, List<String>>> getRegistrations() {
        final Function function = new Function(FUNC_GETREGISTRATIONS, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<DynamicArray<Utf8String>>() {}, new TypeReference<DynamicArray<Address>>() {}));
        return new RemoteFunctionCall<Tuple2<List<String>, List<String>>>(function,
                new Callable<Tuple2<List<String>, List<String>>>() {
                    @Override
                    public Tuple2<List<String>, List<String>> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);
                        return new Tuple2<List<String>, List<String>>(
                                convertToNative((List<Utf8String>) results.get(0).getValue()), 
                                convertToNative((List<Address>) results.get(1).getValue()));
                    }
                });
    }

    public RemoteFunctionCall<SessionInfo> getSessionInfo(byte[] sessionKey_) {
        final Function function = new Function(FUNC_GETSESSIONINFO, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(sessionKey_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<SessionInfo>() {}));
        return executeRemoteCallSingleValueReturn(function, SessionInfo.class);
    }

    public RemoteFunctionCall<byte[]> icaoMasterTreeMerkleRoot() {
        final Function function = new Function(FUNC_ICAOMASTERTREEMERKLEROOT, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<String> implementation() {
        final Function function = new Function(FUNC_IMPLEMENTATION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<Boolean> isOwner(String address_) {
        final Function function = new Function(FUNC_ISOWNER, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, address_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> isPassportFullyRevoked(byte[] passportKey_) {
        final Function function = new Function(FUNC_ISPASSPORTFULLYREVOKED, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<Boolean> isRegistration(String registration_) {
        final Function function = new Function(FUNC_ISREGISTRATION, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, registration_)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteFunctionCall<byte[]> proxiableUUID() {
        final Function function = new Function(FUNC_PROXIABLEUUID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> removeCertificate(byte[] certificateKey_) {
        final Function function = new Function(
                FUNC_REMOVECERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(certificateKey_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> removeOwners(List<String> oldOwners_) {
        final Function function = new Function(
                FUNC_REMOVEOWNERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.Address>(
                        org.web3j.abi.datatypes.Address.class,
                        org.web3j.abi.Utils.typeMap(oldOwners_, org.web3j.abi.datatypes.Address.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> renounceOwnership() {
        final Function function = new Function(
                FUNC_RENOUNCEOWNERSHIP, 
                Arrays.<Type>asList(), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeAllBonds(byte[] passportKey_) {
        final Function function = new Function(
                FUNC_REVOKEALLBONDS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeBond(byte[] passportKey_,
            byte[] sessionKey_) {
        final Function function = new Function(
                FUNC_REVOKEBOND, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_), 
                new org.web3j.abi.datatypes.generated.Bytes32(sessionKey_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeBonds(byte[] passportKey_,
            List<byte[]> sessionKeys_) {
        final Function function = new Function(
                FUNC_REVOKEBONDS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_), 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(sessionKeys_, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeBondsExcept(byte[] passportKey_,
            byte[] keepSessionKey_) {
        final Function function = new Function(
                FUNC_REVOKEBONDSEXCEPT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(passportKey_), 
                new org.web3j.abi.datatypes.generated.Bytes32(keepSessionKey_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> updateRegistrationSet(BigInteger methodId_,
            byte[] data_) {
        final Function function = new Function(
                FUNC_UPDATEREGISTRATIONSET, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint8(methodId_), 
                new org.web3j.abi.datatypes.DynamicBytes(data_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> upgradeToAndCall(String newImplementation,
            byte[] data, BigInteger weiValue) {
        final Function function = new Function(
                FUNC_UPGRADETOANDCALL, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, newImplementation), 
                new org.web3j.abi.datatypes.DynamicBytes(data)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function, weiValue);
    }

    public RemoteFunctionCall<TransactionReceipt> useSignature(byte[] sigHash_) {
        final Function function = new Function(
                FUNC_USESIGNATURE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(sigHash_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<Boolean> usedSignatures(byte[] param0) {
        final Function function = new Function(FUNC_USEDSIGNATURES, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    @Deprecated
    public static StateKeeper load(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        return new StateKeeper(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static StateKeeper load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new StateKeeper(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static StateKeeper load(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        return new StateKeeper(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static StateKeeper load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new StateKeeper(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class CertificateInfo extends StaticStruct {
        public BigInteger expirationTimestamp;

        public CertificateInfo(BigInteger expirationTimestamp) {
            super(new org.web3j.abi.datatypes.generated.Uint64(expirationTimestamp));
            this.expirationTimestamp = expirationTimestamp;
        }

        public CertificateInfo(Uint64 expirationTimestamp) {
            super(expirationTimestamp);
            this.expirationTimestamp = expirationTimestamp.getValue();
        }
    }

    public static class PassportInfo extends StaticStruct {
        public BigInteger activeSessionCount;

        public PassportInfo(BigInteger activeSessionCount) {
            super(new org.web3j.abi.datatypes.generated.Uint64(activeSessionCount));
            this.activeSessionCount = activeSessionCount;
        }

        public PassportInfo(Uint64 activeSessionCount) {
            super(activeSessionCount);
            this.activeSessionCount = activeSessionCount.getValue();
        }
    }

    public static class SessionInfo extends StaticStruct {
        public byte[] activePassport;

        public BigInteger issueTimestamp;

        public SessionInfo(byte[] activePassport, BigInteger issueTimestamp) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(activePassport), 
                    new org.web3j.abi.datatypes.generated.Uint64(issueTimestamp));
            this.activePassport = activePassport;
            this.issueTimestamp = issueTimestamp;
        }

        public SessionInfo(Bytes32 activePassport, Uint64 issueTimestamp) {
            super(activePassport, issueTimestamp);
            this.activePassport = activePassport.getValue();
            this.issueTimestamp = issueTimestamp.getValue();
        }
    }

    public static class BondAddedEventResponse extends BaseEventResponse {
        public byte[] passportKey;

        public byte[] sessionKey;
    }

    public static class BondRevokedEventResponse extends BaseEventResponse {
        public byte[] passportKey;

        public byte[] sessionKey;
    }

    public static class BondSessionReissuedEventResponse extends BaseEventResponse {
        public byte[] passportKey;

        public byte[] sessionKey;
    }

    public static class CertificateAddedEventResponse extends BaseEventResponse {
        public byte[] certificateKey;

        public BigInteger expirationTimestamp;
    }

    public static class CertificateRemovedEventResponse extends BaseEventResponse {
        public byte[] certificateKey;
    }

    public static class InitializedEventResponse extends BaseEventResponse {
        public BigInteger version;
    }

    public static class OwnersAddedEventResponse extends BaseEventResponse {
        public List<String> newOwners;
    }

    public static class OwnersRemovedEventResponse extends BaseEventResponse {
        public List<String> removedOwners;
    }

    public static class UpgradedEventResponse extends BaseEventResponse {
        public String implementation;
    }
}
