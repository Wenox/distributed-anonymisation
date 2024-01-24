import React, {useEffect, useState} from 'react';
import './App.css';
import Dashboard from "./Dashboard";
import axios from "axios";

function App() {
  const [blueprints, setBlueprints] = useState([]);

    useEffect(() => {
        const fetchBlueprints = () => {
            axios.get('/importing/dashboard')
                .then(response => setBlueprints(response.data))
                .catch(error => console.error('Error while fetching the blueprints!', error));
        };

        fetchBlueprints();
        const intervalId = setInterval(fetchBlueprints, 50);
        return () => clearInterval(intervalId);
    }, []);

  // const blueprints = [
  //   { "title": "Employees", "status": "INITIALISED", "blueprintId": "667f2ed9-45de-43e0-8e10-9d52bd78cd4c" },
  //   { "title": "Medical docs", "status": "STALE", "blueprintId": "dbfc40a0-64b1-42e6-89c3-17ec840082a6" },
  //   { "title": "Transactions", "status": "METADATA_EXTRACTION_SUCCESS", "blueprintId": "26f35239-25a9-449e-810b-6cdcde0a4e85" },
  //   { "title": "Customer info", "status": "METADATA_EXTRACTION_FAILURE", "blueprintId": "67990002-3788-43f0-8fb9-2c47d94563c6" },
  //   { "title": "Traffic data", "status": "DUMP_STORE_FAILURE", "blueprintId": "19059bb4-4b1e-4072-9ce8-2ab5762565a2" },
  // ];

  return <Dashboard blueprints={blueprints} />;
}

export default App;
