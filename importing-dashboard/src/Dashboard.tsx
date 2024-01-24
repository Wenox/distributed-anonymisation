import React from 'react';
import StatusBoxes from './StatusBoxes';
import './Dashboard.css';

type Blueprint = {
    title: string;
    status: string;
    blueprintId: string;
};

type DashboardProps = {
    blueprints: Blueprint[];
};

const Dashboard: React.FC<DashboardProps> = ({ blueprints }) => {
    const isBlueprintsEmpty = !blueprints || blueprints.length === 0;

    return (
        <div className="dashboard-container">
            <h1>Importing process dashboard</h1>
            {isBlueprintsEmpty ? (
                <h3 style={{ color: 'red' }}>No saga instances in the system...</h3>
            ) : (
                blueprints.map(blueprint => (
                    <div key={blueprint.blueprintId} className="blueprint-row">
                        <div className="blueprint-info">
                            <h3>{blueprint.title}</h3>
                            <p>{blueprint.blueprintId}</p>
                        </div>
                        <StatusBoxes status={blueprint.status} type={determineType(blueprint.status)} />
                    </div>
                ))
            )}
        </div>
    );
};

function determineType(status: string): 'stale' | 'success' | 'failure' {
    switch (status) {
        case "STALE":
            return 'stale';
        case "INITIALISED":
        case "DUMP_STORE_SUCCESS":
        case "RESTORE_SUCCESS":
        case "METADATA_EXTRACTION_SUCCESS":
            return 'success';
        case "DUMP_STORE_FAILURE":
        case "RESTORE_FAILURE":
        case "METADATA_EXTRACTION_FAILURE":
            return 'failure';
        default:
            return 'failure';
    }
}

export default Dashboard;
