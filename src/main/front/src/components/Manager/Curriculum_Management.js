import React, { useState, useEffect } from 'react';
import { CirclePicker } from 'react-color';
import axios from 'axios';
import './Curriculum_Management.css';
import './Modal.css';

axios.defaults.baseURL = 'http://localhost:8080';

const CurriculumManagement = () => {
  const [curriculums, setCurriculums] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isColorPickerOpen, setIsColorPickerOpen] = useState(false);
  const [newCurriculum, setNewCurriculum] = useState({
    full_name: '',
    startDate: '',
    endDate: '',
    color: '#FF0000',
    teacher: '',
  });

  const getToken = () => {
    return localStorage.getItem('token');
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setNewCurriculum({ ...newCurriculum, [name]: value });
  };

  const handleCourseChange = (courseName) => {
    setNewCurriculum({ ...newCurriculum, full_name: courseName });
  };

  const handleColorChange = (color) => {
    setNewCurriculum({ ...newCurriculum, color: color.hex });
    setIsColorPickerOpen(false);
  };

  const handleAddCurriculum = async () => {
    const newCurriculumItem = {
      full_name: newCurriculum.full_name,
      startDate: newCurriculum.startDate,
      endDate: newCurriculum.endDate,
      color: newCurriculum.color,
      teacher: newCurriculum.teacher,
    };

    try {
      const token = getToken();
      const response = await axios.post('/managers/manage-curriculums/enroll', newCurriculumItem, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      if (response.status === 200) {
        setIsModalOpen(false);
        fetchCurriculums();
      } else {
        console.error('교육 과정 등록 실패');
      }
    } catch (error) {
      console.error('Error:', error);
    }
  };

  const fetchCurriculums = async (type = 'NCP') => {
    try {
      const token = getToken();
      const response = await axios.get(`/managers/manage-curriculums/${type}`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setCurriculums(response.data);
    } catch (error) {
      console.error('Error:', error);
    }
  };

  useEffect(() => {
    fetchCurriculums();
  }, []);

  return (
    <div className="curriculum-management">
      <h1>교육 과정</h1>
      <div className="button-container">
        <button className="curriculum-add-button" onClick={() => setIsModalOpen(true)}>교육 과정 추가</button>
      </div>
      <div className="curriculum-container">
        <div className="curriculum-column">
          <div className="curriculum-list">
            {curriculums.filter(curriculum => curriculum.full_name.includes('네이버')).map(curriculum => (
              <div key={curriculum.id} className="curriculum-card">
                <div className="curriculum-header">
                  <span className="curriculum-th" style={{ backgroundColor: curriculum.color }}>{curriculum.th}</span>
                  <span className="curriculum-full_name">{curriculum.full_name}</span>
                </div>
                <div className="curriculum-footer">
                  <span className="curriculum-teacher">강사 {curriculum.teacher}</span>
                  <span className="curriculum-students">
                    <i className="fas fa-user"></i> {curriculum.students}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
        <div className="curriculum-column">
          <div className="curriculum-list">
            {curriculums.filter(curriculum => curriculum.full_name.includes('AWS')).map(curriculum => (
              <div key={curriculum.id} className="curriculum-card">
                <div className="curriculum-header">
                  <span className="curriculum-th" style={{ backgroundColor: curriculum.color }}>{curriculum.th}</span>
                  <span className="curriculum-full_name">{curriculum.full_name}</span>
                </div>
                <div className="curriculum-footer">
                  <span className="curriculum-teacher">강사 {curriculum.teacher}</span>
                  <span className="curriculum-students">
                    <i className="fas fa-user"></i> {curriculum.students}
                  </span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      {isModalOpen && (
        <div className="modal-overlay">
          <div className="modal-content">
            <button className="modal-close" onClick={() => setIsModalOpen(false)}>×</button>
            <span className="curriculum-submit">교육 과정 등록</span>
            <div className="course-selection">
              <button className={`course-button ${newCurriculum.full_name === '네이버 클라우드 데브옵스 과정' ? 'selected' : ''}`} onClick={() => handleCourseChange('네이버 클라우드 데브옵스 과정')}>네이버 클라우드</button>
              <button className={`course-button ${newCurriculum.full_name === 'AWS 클라우드 데브옵스 과정' ? 'selected' : ''}`} onClick={() => handleCourseChange('AWS 클라우드 데브옵스 과정')}>AWS</button>
            </div>
            <div className="curriculum-input-group">
              <label>시작일</label>
              <input type="date" name="startDate" value={newCurriculum.startDate} onChange={handleInputChange} />
            </div>
            <div className="curriculum-input-group">
              <label>종료일</label>
              <input type="date" name="endDate" value={newCurriculum.endDate} onChange={handleInputChange} />
            </div>
            <div className="curriculum-input-group">
              <label>기수 색상</label>
              <input
                type="text"
                value={newCurriculum.color}
                readOnly
              />
              <div className="color-input-select" onClick={() => setIsColorPickerOpen(true)}>
                <div className="color-box" style={{ backgroundColor: newCurriculum.color }}></div>
              </div>
            </div>
            {isColorPickerOpen && (
              <div className="color-picker-modal-overlay" onClick={() => setIsColorPickerOpen(false)}>
                <div className="color-picker-modal-content" onClick={(e) => e.stopPropagation()}>
                  <CirclePicker
                    color={newCurriculum.color}
                    onChangeComplete={handleColorChange}
                    colors={["#F3C41E", "#F58D11", "#B85B27", "#A90C57", "#F45CE5", "#AE59F0", "#0A8735", "#6F961E", "#19E308", "#1D1AA6", "#20CFF5", "#98B3E5"]}
                  />
                </div>
              </div>
            )}
            <div className="curriculum-input-group">
              <label>강사</label>
              <input type="text" name="teacher" value={newCurriculum.teacher} onChange={handleInputChange} />
            </div>
            <div className="modal-actions">
              <button className="modal-button" onClick={handleAddCurriculum}>교육 과정 등록</button>
              <button className="modal-button" onClick={() => setIsModalOpen(false)}>등록 취소</button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default CurriculumManagement;