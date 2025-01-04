import React, { useState } from 'react';
import { Modal, Button } from 'react-bootstrap';
import trashIcon from 'C:/Users/LEGION/my-news-react-app/news-application/src/components/news/images/trash-icon.png';

import './DeleteNewsModal.css';



const DeleteNewsModal = ({ show, handleClose, selectedNewsId, onNewsDeleted }) => {

    const BASE_URL = 'http://localhost:8082'; 
    const [modalError, setModalError] = useState('');

    const handleDeleteNews = async () => {
        try {
            const token = localStorage.getItem('authToken');
            if (!token) {
                throw new Error('User is not authenticated');
            }
        const response = await fetch(`${BASE_URL}/api/v1/news/${selectedNewsId}`, {
            method: 'DELETE',
            headers:{
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
        });

        if(!response.ok){
            const errorData = response.headers.get('content-type')?.includes('application/json')
            ? await response.json()
            : { message: 'Error deleting news' };
            console.log(selectedNewsId);
        throw new Error(errorData.message || 'Error deleting news');
        }
        
        onNewsDeleted();
            handleClose(); 
        } catch (err) {
            setModalError(err.message);
            }
        
        }

    return (
<Modal show={show} onHide={handleClose} centered className="delete-modal"> 
<Modal.Header className="delete-modal-top" closeButton></Modal.Header>
<Modal.Body className="text-centr">
    <img src={trashIcon} alt="Trash icon" className="trash-icon" />
<h5> Do you really want to delete this news? </h5>
{modalError && <div className="alert alert-danger">{modalError}</div>} 
</Modal.Body>
<Modal.Footer className="justify-content-center">
 <button  className="btn btn-outline-secondary  p-2 mb-4" onClick={handleClose}>Cancel</button>
<button className="btn btn-danger  p-2 ms-auto mb-4" onClick={handleDeleteNews}>Delete </button> 
</Modal.Footer>
</Modal>

    )}
    export default DeleteNewsModal;
    