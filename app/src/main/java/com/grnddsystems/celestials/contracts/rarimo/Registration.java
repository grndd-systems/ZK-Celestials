package com.grnddsystems.celestials.contracts.rarimo;

import io.reactivex.Flowable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
// CustomError not available in this web3j version
// import org.web3j.abi.datatypes.CustomError;
import org.web3j.abi.datatypes.DynamicBytes;
import org.web3j.abi.datatypes.DynamicStruct;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
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
public class Registration extends Contract {
    public static final String BINARY = "Bin file was not provided";

    public static final String FUNC_P_NO_AA = "P_NO_AA";

    public static final String FUNC_UPGRADE_INTERFACE_VERSION = "UPGRADE_INTERFACE_VERSION";

    public static final String FUNC___REGISTRATION_INIT = "__Registration_init";

    public static final String FUNC_CERTIFICATEDISPATCHERS = "certificateDispatchers";

    public static final String FUNC_IMPLEMENTATION = "implementation";

    public static final String FUNC_PASSPORTDISPATCHERS = "passportDispatchers";

    public static final String FUNC_PASSPORTVERIFIERS = "passportVerifiers";

    public static final String FUNC_PROXIABLEUUID = "proxiableUUID";

    public static final String FUNC_REGISTERCERTIFICATE = "registerCertificate";

    public static final String FUNC_REGISTERVIANOIR = "registerViaNoir";

    public static final String FUNC_REVOKE = "revoke";

    public static final String FUNC_REVOKEALLSESSIONS = "revokeAllSessions";

    public static final String FUNC_REVOKECERTIFICATE = "revokeCertificate";

    public static final String FUNC_REVOKEOTHERSESSIONS = "revokeOtherSessions";

    public static final String FUNC_STATEKEEPER = "stateKeeper";

    public static final String FUNC_UPDATEDEPENDENCY = "updateDependency";

    public static final String FUNC_UPGRADETOANDCALL = "upgradeToAndCall";

    // CustomError not available in this web3j version - commented out
    /*
    public static final CustomError ADDRESSEMPTYCODE_ERROR = new CustomError("AddressEmptyCode",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));

    public static final CustomError ERC1967INVALIDIMPLEMENTATION_ERROR = new CustomError("ERC1967InvalidImplementation",
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));

    public static final CustomError ERC1967NONPAYABLE_ERROR = new CustomError("ERC1967NonPayable",
            Arrays.<TypeReference<?>>asList());

    public static final CustomError FAILEDCALL_ERROR = new CustomError("FailedCall",
            Arrays.<TypeReference<?>>asList());

    public static final CustomError INVALIDINITIALIZATION_ERROR = new CustomError("InvalidInitialization",
            Arrays.<TypeReference<?>>asList());

    public static final CustomError INVALIDNOIRPROOF_ERROR = new CustomError("InvalidNoirProof",
            Arrays.<TypeReference<?>>asList(new TypeReference<DynamicBytes>() {}, new TypeReference<DynamicArray<Bytes32>>() {}));

    public static final CustomError NOTINITIALIZING_ERROR = new CustomError("NotInitializing",
            Arrays.<TypeReference<?>>asList());

    public static final CustomError UUPSUNAUTHORIZEDCALLCONTEXT_ERROR = new CustomError("UUPSUnauthorizedCallContext",
            Arrays.<TypeReference<?>>asList());

    public static final CustomError UUPSUNSUPPORTEDPROXIABLEUUID_ERROR = new CustomError("UUPSUnsupportedProxiableUUID",
            Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
    */

    public static final Event INITIALIZED_EVENT = new Event("Initialized", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Uint64>() {}));
    ;

    public static final Event UPGRADED_EVENT = new Event("Upgraded", 
            Arrays.<TypeReference<?>>asList(new TypeReference<Address>(true) {}));
    ;

    @Deprecated
    protected Registration(String contractAddress, Web3j web3j, Credentials credentials,
            BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Registration(String contractAddress, Web3j web3j, Credentials credentials,
            ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, credentials, contractGasProvider);
    }

    @Deprecated
    protected Registration(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    protected Registration(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, contractGasProvider);
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

    public RemoteFunctionCall<byte[]> P_NO_AA() {
        final Function function = new Function(FUNC_P_NO_AA, 
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

    public RemoteFunctionCall<TransactionReceipt> __Registration_init(String stateKeeper_) {
        final Function function = new Function(
                FUNC___REGISTRATION_INIT, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(160, stateKeeper_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> certificateDispatchers(byte[] param0) {
        final Function function = new Function(FUNC_CERTIFICATEDISPATCHERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> implementation() {
        final Function function = new Function(FUNC_IMPLEMENTATION, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> passportDispatchers(byte[] param0) {
        final Function function = new Function(FUNC_PASSPORTDISPATCHERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<String> passportVerifiers(byte[] param0) {
        final Function function = new Function(FUNC_PASSPORTVERIFIERS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(param0)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<byte[]> proxiableUUID() {
        final Function function = new Function(FUNC_PROXIABLEUUID, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bytes32>() {}));
        return executeRemoteCallSingleValueReturn(function, byte[].class);
    }

    public RemoteFunctionCall<TransactionReceipt> registerCertificate(Certificate certificate_,
            ICAOMember icaoMember_, List<byte[]> icaoMerkleProof_) {
        final Function function = new Function(
                FUNC_REGISTERCERTIFICATE, 
                Arrays.<Type>asList(certificate_, 
                icaoMember_, 
                new org.web3j.abi.datatypes.DynamicArray<org.web3j.abi.datatypes.generated.Bytes32>(
                        org.web3j.abi.datatypes.generated.Bytes32.class,
                        org.web3j.abi.Utils.typeMap(icaoMerkleProof_, org.web3j.abi.datatypes.generated.Bytes32.class))), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> registerViaNoir(byte[] certificatesRoot_,
            BigInteger sessionKey_, BigInteger dgCommit_, Passport passport_, byte[] zkPoints_) {
        final Function function = new Function(
                FUNC_REGISTERVIANOIR, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(certificatesRoot_), 
                new org.web3j.abi.datatypes.generated.Uint256(sessionKey_), 
                new org.web3j.abi.datatypes.generated.Uint256(dgCommit_), 
                passport_, 
                new org.web3j.abi.datatypes.DynamicBytes(zkPoints_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revoke(BigInteger sessionKey_,
            Passport passport_) {
        final Function function = new Function(
                FUNC_REVOKE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(sessionKey_), 
                passport_), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeAllSessions(BigInteger sessionKey_,
            Passport passport_) {
        final Function function = new Function(
                FUNC_REVOKEALLSESSIONS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(sessionKey_), 
                passport_), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeCertificate(byte[] certificateKey_) {
        final Function function = new Function(
                FUNC_REVOKECERTIFICATE, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Bytes32(certificateKey_)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<TransactionReceipt> revokeOtherSessions(BigInteger keepSessionKey_,
            Passport passport_) {
        final Function function = new Function(
                FUNC_REVOKEOTHERSESSIONS, 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.generated.Uint256(keepSessionKey_), 
                passport_), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteFunctionCall<String> stateKeeper() {
        final Function function = new Function(FUNC_STATEKEEPER, 
                Arrays.<Type>asList(), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    public RemoteFunctionCall<TransactionReceipt> updateDependency(BigInteger methodId_,
            byte[] data_) {
        final Function function = new Function(
                FUNC_UPDATEDEPENDENCY, 
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

    @Deprecated
    public static Registration load(String contractAddress, Web3j web3j,
            Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Registration(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    @Deprecated
    public static Registration load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Registration(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static Registration load(String contractAddress, Web3j web3j,
            Credentials credentials, ContractGasProvider contractGasProvider) {
        return new Registration(contractAddress, web3j, credentials, contractGasProvider);
    }

    public static Registration load(String contractAddress, Web3j web3j,
            TransactionManager transactionManager, ContractGasProvider contractGasProvider) {
        return new Registration(contractAddress, web3j, transactionManager, contractGasProvider);
    }

    public static class Certificate extends DynamicStruct {
        public byte[] dataType;

        public byte[] signedAttributes;

        public BigInteger keyOffset;

        public BigInteger expirationOffset;

        public Certificate(byte[] dataType, byte[] signedAttributes, BigInteger keyOffset,
                BigInteger expirationOffset) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(dataType), 
                    new org.web3j.abi.datatypes.DynamicBytes(signedAttributes), 
                    new org.web3j.abi.datatypes.generated.Uint256(keyOffset), 
                    new org.web3j.abi.datatypes.generated.Uint256(expirationOffset));
            this.dataType = dataType;
            this.signedAttributes = signedAttributes;
            this.keyOffset = keyOffset;
            this.expirationOffset = expirationOffset;
        }

        public Certificate(Bytes32 dataType, DynamicBytes signedAttributes, Uint256 keyOffset,
                Uint256 expirationOffset) {
            super(dataType, signedAttributes, keyOffset, expirationOffset);
            this.dataType = dataType.getValue();
            this.signedAttributes = signedAttributes.getValue();
            this.keyOffset = keyOffset.getValue();
            this.expirationOffset = expirationOffset.getValue();
        }
    }

    public static class ICAOMember extends DynamicStruct {
        public byte[] signature;

        public byte[] publicKey;

        public ICAOMember(byte[] signature, byte[] publicKey) {
            super(new org.web3j.abi.datatypes.DynamicBytes(signature), 
                    new org.web3j.abi.datatypes.DynamicBytes(publicKey));
            this.signature = signature;
            this.publicKey = publicKey;
        }

        public ICAOMember(DynamicBytes signature, DynamicBytes publicKey) {
            super(signature, publicKey);
            this.signature = signature.getValue();
            this.publicKey = publicKey.getValue();
        }
    }

    public static class Passport extends DynamicStruct {
        public byte[] dataType;

        public byte[] zkType;

        public byte[] signature;

        public byte[] publicKey;

        public byte[] passportHash;

        public Passport(byte[] dataType, byte[] zkType, byte[] signature, byte[] publicKey,
                byte[] passportHash) {
            super(new org.web3j.abi.datatypes.generated.Bytes32(dataType), 
                    new org.web3j.abi.datatypes.generated.Bytes32(zkType), 
                    new org.web3j.abi.datatypes.DynamicBytes(signature), 
                    new org.web3j.abi.datatypes.DynamicBytes(publicKey), 
                    new org.web3j.abi.datatypes.generated.Bytes32(passportHash));
            this.dataType = dataType;
            this.zkType = zkType;
            this.signature = signature;
            this.publicKey = publicKey;
            this.passportHash = passportHash;
        }

        public Passport(Bytes32 dataType, Bytes32 zkType, DynamicBytes signature,
                DynamicBytes publicKey, Bytes32 passportHash) {
            super(dataType, zkType, signature, publicKey, passportHash);
            this.dataType = dataType.getValue();
            this.zkType = zkType.getValue();
            this.signature = signature.getValue();
            this.publicKey = publicKey.getValue();
            this.passportHash = passportHash.getValue();
        }
    }

    public static class InitializedEventResponse extends BaseEventResponse {
        public BigInteger version;
    }

    public static class UpgradedEventResponse extends BaseEventResponse {
        public String implementation;
    }
}