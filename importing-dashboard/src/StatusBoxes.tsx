import React from 'react';

type StatusBoxesProps = {
    status: string;
    type: 'stale' | 'success' | 'failure';
};

type FailureStatus = 'DUMP_STORE_FAILURE' | 'RESTORE_FAILURE' | 'METADATA_EXTRACTION_FAILURE';

// This is just a POC code "written" in 5 minutes that does the job.
const StatusBoxes: React.FC<StatusBoxesProps> = ({ status, type }) => {
    let boxes = [];

    if (type === 'stale') {
        boxes.push(<div key="green" className="status-box green">Initialized</div>);
        boxes.push(<div key="red" className="status-box red">Stale</div>);
    } else if (type === 'success') {
        const texts = ["Initialized", "Dump stored", "Database restored", "Metadata extracted"];
        let activeCount = status === "INITIALIZED" ? 1 :
            status === "DUMP_STORE_SUCCESS" ? 2 :
                status === "RESTORE_SUCCESS" ? 3 : 4;
        for (let i = 0; i < 4; i++) {
            boxes.push(
                <div key={i} className={`status-box ${i < activeCount ? 'green' : 'pending'}`}>{texts[i]}</div>
            );
        }
    } else if (type === 'failure') {
        const texts: { [key in FailureStatus]: string[] } = {
            "DUMP_STORE_FAILURE": ["Initialized", "Dump store failed", "", ""],
            "RESTORE_FAILURE": ["Initialized", "Dump stored", "Database restore failed", ""],
            "METADATA_EXTRACTION_FAILURE": ["Initialized", "Dump stored", "Database restored", "Metadata extraction failed"]
        };

        let activeCount = status === "DUMP_STORE_FAILURE" ? 2 :
            status === "RESTORE_FAILURE" ? 3 : 4;
        for (let i = 0; i < activeCount; i++) {
            boxes.push(
                <div key={i} className={`status-box ${i < activeCount - 1 ? 'green' : i === activeCount ? 'red' : 'red'}`}>
                    {texts[status as FailureStatus][i]}
                </div>
            );
        }
    }

    return <div className="status-boxes">{boxes}</div>;
};

export default StatusBoxes;
